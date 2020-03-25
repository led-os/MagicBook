package com.key.keylibrary.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * created by key  on 2019/5/4
 */
public class ImageLoader {

    private static ImageLoader mInstance;

    private LruCache<String, Bitmap> mLruCache;

    private ExecutorService mThreadPool;
    private static final int DEAFULT_THREAD_COUNT = 1;


    private Type mType = Type.LIFO;


    private LinkedList<Runnable> mTaskQueue;

    private Thread mPoolThread;
    private Handler mPoolThreadHandler;


    private Handler mUIHandler;
    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);


    private Semaphore mSemaphoreThreadPool;
    public enum Type{
        FIF0,LIFO;
    }
    private ImageLoader(int threadCount, Type type){
        init(threadCount,type);
    }

    public static  ImageLoader getInstance(){
        if(mInstance == null){
            synchronized (ImageLoader.class){

                if(mInstance == null){
                    mInstance = new ImageLoader(DEAFULT_THREAD_COUNT, Type.LIFO);
                }
            }

        }
        return  mInstance;
    }


    public static  ImageLoader getInstance(int threadCount,Type type){
        if(mInstance == null){
            synchronized (ImageLoader.class){

                if(mInstance == null){
                    mInstance = new ImageLoader(threadCount,type);
                }
            }

        }
        return  mInstance;
    }

    private void init(int threadCount, Type type) {


        mPoolThread = new Thread(){
            @SuppressLint("HandlerLeak")
            @Override
            public void run() {

                Looper.prepare();
                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {

                        mThreadPool.execute(getTask());

                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };

        mPoolThread.start();

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory/8;
        mLruCache = new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };



        mThreadPool = Executors.newFixedThreadPool(threadCount);

        mTaskQueue = new LinkedList<Runnable>();
        mType = type;


        mSemaphoreThreadPool = new Semaphore(threadCount);
    }



    @SuppressLint("HandlerLeak")
    public void loadImage(final String path, final ImageView imageView){
        imageView.setTag(path);


        if(mUIHandler == null ){
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageView = holder.imageView;
                    String path = holder.path;

                    if(imageView.getTag().toString().equals(path)){
                        imageView.setImageBitmap(bm);
                    }
                }
            };
        }



        Bitmap bm = getBitmapFromLruCache(path);
        if(bm != null){
            refreshBitmap(path,imageView,bm);
        }else{
            addTask(new Runnable(){
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {

                   ImageSize imageSize =  getImageViewSize(imageView);
                   Bitmap bm = decodeSampleBitmapFromPath(path,imageSize.width,imageSize.height);
                   addBitmapToLruCache(path,bm);

                   refreshBitmap(path,imageView,bm);
                   mSemaphoreThreadPool.release();
                }
            });
        }
    }


    public void refreshBitmap(String path,ImageView imageView, Bitmap bm){
        Message message = Message.obtain();
        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bm;
        holder.path = path;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }


    private void addBitmapToLruCache(String path, Bitmap bm) {
        if(getBitmapFromLruCache(path)== null){

            if(bm != null){
                mLruCache.put(path,bm);
            }
        }
    }


    protected Bitmap decodeSampleBitmapFromPath(String path,int width,int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        options.inSampleSize = caculateInSampleSize(options,width,height);


        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int width = options.outWidth;
        int height = options.outHeight;


        int inSampleSize = 1;
        if(width > reqWidth || height > reqHeight){
            int widthRadio = Math.round(width * 1.0f/reqWidth);
            int heightRadio = Math.round(height * 1.0f/reqHeight);

            inSampleSize = Math.max(widthRadio,heightRadio);
        }
        return inSampleSize;
    }


    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth();
        if(width <= 0){
            width = lp.width;
        }
        if(width <= 0){
            width = getImageViewFieldValue(imageView,"mMaxWidth");
        }
        if(width <= 0){
            width = displayMetrics.widthPixels;
        }


        int height = imageView.getHeight();
        if(height <= 0){
            width = lp.height;
        }
        if(height <= 0){
            height = getImageViewFieldValue(imageView,"mMaxHeight");
        }
        if(height <= 0){
            height = displayMetrics.heightPixels;
        }

        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    private synchronized  void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        try {
            if(mPoolThreadHandler == null){
                mSemaphorePoolThreadHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Runnable getTask() {

        if(mType == Type.FIF0){
            return mTaskQueue.removeFirst();
        }else if(mType == Type.LIFO){
            return  mTaskQueue.removeLast();
        }


        return null;
    }


    private Bitmap getBitmapFromLruCache(String path) {
        return  mLruCache.get(path);
    }


    private class ImageSize{
        int width;
        int height;
    }

    private  class ImgBeanHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

    /**
     *    通过反射获取imageView 的某个属性值
     * @param object
     * @param fieldName
     * @return
     */
    public static int getImageViewFieldValue(Object object,String fieldName){
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if(fieldValue > 0 && fieldValue < Integer.MAX_VALUE){
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
