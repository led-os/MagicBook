package com.key.magicbook.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.key.keylibrary.utils.UiUtils;
import com.key.magicbook.R;
import com.key.magicbook.base.BaseContract;

import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * created by key  on 2020/3/3
 */
public class GlideUtils {

    public static void loadCenter(Context context, String url, ImageView iv) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(false)
                .transform(new CenterCrop())
                .placeholder(R.mipmap.loadding);
        loadImage(context, url, options, iv);
    }

    public static void load(Context context, String url, ImageView iv) {
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)
                .transform(new GlideRoundTransform(context, UiUtils.dip2px(2f)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.fitCenter()
                //fitCenter 缩放图片充满ImageView CenterInside大缩小原(图) CenterCrop大裁小扩充满ImageView  Center大裁(中间)小原
                .error(R.mipmap.test_2);
        loadImage(context, url, options, iv);
    }


    public static void loadBlur(Context context, String url, View iv) {
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)
                .transform(new BlurTransformation(25, 20))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.fitCenter()
                //fitCenter 缩放图片充满ImageView CenterInside大缩小原(图) CenterCrop大裁小扩充满ImageView  Center大裁(中间)小原
                .error(R.mipmap.test_2);
        try {
            loadBlurImage(context, url, options, iv);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void loadGif(Context context, ImageView iv) {
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)
                .transform(new GlideRoundTransform(context, UiUtils.dip2px(2f)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.fitCenter()
                // fitCenter 缩放图片充满ImageView CenterInside大缩小原(图) CenterCrop大裁小扩充满ImageView  Center大裁(中间)小原
                .error(R.mipmap.test_2);
        if(!isDestroy((Activity) context)){
            Glide.with(context)
                    .asGif()
                    .load(R.mipmap.loadding)
                    .apply(options).into(iv);
        }

    }

    private static void loadImage(Context context, String url, RequestOptions options, ImageView view) {
        if(!isDestroy((Activity) context)){
            Glide.with(context)
                    .load(url)
                    .apply(options)
                    .thumbnail(Glide.with(view)
                            .load(R.mipmap.loadding)
                             .apply(options)).into(view);
        }

    }


    private static void loadBlurImage(Context context, String url, RequestOptions options, View view) throws ExecutionException, InterruptedException {


        if(!isDestroy((Activity) context)){
            FutureTarget<Drawable> into = Glide.with(context)
                    .load(url)
                    .apply(options)
                    .into(1000, 1000);

               Drawable drawable = into.get();
               ((Activity) context).runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       view.setBackground(drawable);
                   }
               });




        }

    }



    //Glide.with(context).load(R.drawable.defalut_photo);

    public static boolean isDestroy(Activity mActivity) {
        if (mActivity== null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }
}
