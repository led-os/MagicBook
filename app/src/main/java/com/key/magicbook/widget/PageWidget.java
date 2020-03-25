package com.key.magicbook.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.key.magicbook.util.PageFactory;
import com.key.magicbook.widget.animation.AnimationProvider;
import com.key.magicbook.widget.animation.CoverAnimation;
import com.key.magicbook.widget.animation.NoneAnimation;
import com.key.magicbook.widget.animation.SimulationAnimation;
import com.key.magicbook.widget.animation.SlideAnimation;
/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class PageWidget extends View {
    private final static String TAG = "BookPageWidget";

    private Context mContext;


    /**
     * @params mScreenWidth 屏幕宽
     * @params mScreenHeight 屏幕高
     */
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;


    /**
     *@params isMove 是否移动了
     *@params isNext 是否翻到下一页
     *@params cancelPage 是否取消翻页
     *@params noNext  是否没下一页或者上一页
     */
    private Boolean isMove = false;
    private Boolean isNext = false;
    private Boolean cancelPage = false;
    private Boolean noNext = false;

    /**
     *  手指按压点
     * @params downX
     * @params downY
     */
    private int downX = 0;
    private int downY = 0;


    /**
     *  手指移动点
     * @params moveX
     * @params moveY
     */
    private int moveX = 0;
    private int moveY = 0;


    /**
     *  动画是否执行
     *  @params isRunning
     */
    private Boolean isRunning =false;


    /**
     *  @params mCurPageBitmap 当前页
     *  @params mNextPageBitmap 下一页
     */
    Bitmap mCurPageBitmap = null;
    Bitmap mNextPageBitmap = null;


    /**
     * @params mAnimationProvider 动画提供者
     */
    private AnimationProvider mAnimationProvider;


    /**
     *@params mScroller 界面滑动主体
     *@params mBgColor 界面背景颜色
     */
    Scroller mScroller;
    private int mBgColor = 0xFFCEC29C;


    /**
     * @params mTouchListener 触摸监听
     */
    private TouchListener mTouchListener;


    /**
     * 构造方法
     * @param context
     */
    public PageWidget(Context context) {
        this(context,null);
    }


    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public PageWidget(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }


    /**
     * 构造方法
     *   初始化上下文环境
     *   初始化布局主体滑动Scroller
     *   初始化布局动画提供者
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public PageWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initPage();
        mScroller = new Scroller(getContext(),new LinearInterpolator());
        mAnimationProvider = new SimulationAnimation(mCurPageBitmap,mNextPageBitmap,mScreenWidth,mScreenHeight);
    }


    /**
     * 初始化布局
     *   获取窗口管理者 windowManager 并初始化窗口布局宽高
     *   android:LargeHeap=true  use in  manifest application
     *
     *   新建两个Bitmap : mCurPageBitmap,mNextPageBitmap
     */
    private void initPage(){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        mCurPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
        mNextPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
    }


    /**
     * 根据 pageMode 生成相对应的 页面动画提供者
     * @param pageMode
     */
    public void setPageMode(int pageMode){
        switch (pageMode){
            case Config.PAGE_MODE_COVER:
                mAnimationProvider = new CoverAnimation(mCurPageBitmap,mNextPageBitmap,mScreenWidth,mScreenHeight);
                break;
            case Config.PAGE_MODE_SLIDE:
                mAnimationProvider = new SlideAnimation(mCurPageBitmap,mNextPageBitmap,mScreenWidth,mScreenHeight);
                break;
            case Config.PAGE_MODE_NONE:
                mAnimationProvider = new NoneAnimation(mCurPageBitmap,mNextPageBitmap,mScreenWidth,mScreenHeight);
                break;
            case Config.PAGE_MODE_SIMULATION:
            default:
                mAnimationProvider = new SimulationAnimation(mCurPageBitmap,mNextPageBitmap,mScreenWidth,mScreenHeight);
        }
    }


    /**
     *  获取当前页
     * @return Bitmap
     */
    public Bitmap getCurPage(){
        return mCurPageBitmap;
    }


    /**
     *  获取下一页或者上一页
     * @return  Bitmap
     */
    public Bitmap getNextPage(){
        return mNextPageBitmap;
    }


    /**
     *  设置背景颜色
     * @param color
     */
    public void setBgColor(int color){
        mBgColor = color;
    }


    /**
     * 绘制
     * @param canvas 画板
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBgColor);
        if (isRunning) {
            mAnimationProvider.drawMove(canvas);
        } else {
            mAnimationProvider.drawStatic(canvas);
        }
    }


    /**
     * 设置触摸监听
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (PageFactory.getStatus() == PageFactory.Status.OPENING){
            return true;
        }

        int x = (int)event.getX();
        int y = (int)event.getY();

        mAnimationProvider.setTouchPoint(x,y);
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            downX = (int) event.getX();
            downY = (int) event.getY();
            moveX = 0;
            moveY = 0;
            isMove = false;
//            cancelPage = false;
            noNext = false;
            isNext = false;
            isRunning = false;
            mAnimationProvider.setStartPoint(downX,downY);
            abortAnimation();
            Log.e(TAG,"ACTION_DOWN");
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){

            final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            //判断是否移动了
            if (!isMove) {
                isMove = Math.abs(downX - x) > slop || Math.abs(downY - y) > slop;
            }

            if (isMove){
                isMove = true;
                if (moveX == 0 && moveY ==0) {
                    Log.e(TAG,"isMove");
                    //判断翻得是上一页还是下一页
                    if (x - downX >0){
                        isNext = false;
                    }else{
                        isNext = true;
                    }
                    cancelPage = false;
                    if (isNext) {
                        Boolean isNext = mTouchListener.nextPage();
                        mAnimationProvider.setDirection(AnimationProvider.Direction.next);

                        if (!isNext) {
                            noNext = true;
                            return true;
                        }
                    } else {
                        Boolean isPre = mTouchListener.prePage();
                        mAnimationProvider.setDirection(AnimationProvider.Direction.pre);

                        if (!isPre) {
                            noNext = true;
                            return true;
                        }
                    }
                    Log.e(TAG,"isNext:" + isNext);
                }else{
                    if (isNext){
                        if (x - moveX > 0){
                            cancelPage = true;
                            mAnimationProvider.setCancel(true);
                        }else {
                            cancelPage = false;
                            mAnimationProvider.setCancel(false);
                        }
                    }else{
                        if (x - moveX < 0){
                            mAnimationProvider.setCancel(true);
                            cancelPage = true;
                        }else {
                            mAnimationProvider.setCancel(false);
                            cancelPage = false;
                        }
                    }
                    Log.e(TAG,"cancelPage:" + cancelPage);
                }

                moveX = x;
                moveY = y;
                isRunning = true;
                this.postInvalidate();
            }
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            Log.e(TAG,"ACTION_UP");
            if (!isMove){
                cancelPage = false;
                if (downX > mScreenWidth / 5 && downX < mScreenWidth * 4 / 5 && downY > mScreenHeight / 3 && downY < mScreenHeight * 2 / 3){
                    if (mTouchListener != null){
                        mTouchListener.center();
                    }
                    Log.e(TAG,"center");
                    return true;
                }else if (x < mScreenWidth / 2){
                    isNext = false;
                }else{
                    isNext = true;
                }

                if (isNext) {
                    Boolean isNext = mTouchListener.nextPage();
                    mAnimationProvider.setDirection(AnimationProvider.Direction.next);
                    if (!isNext) {
                        return true;
                    }
                } else {
                    Boolean isPre = mTouchListener.prePage();
                    mAnimationProvider.setDirection(AnimationProvider.Direction.pre);
                    if (!isPre) {
                        return true;
                    }
                }
            }

            if (cancelPage && mTouchListener != null){
                mTouchListener.cancel();
            }

            Log.e(TAG,"isNext:" + isNext);
            if (!noNext) {
                isRunning = true;
                mAnimationProvider.startAnimation(mScroller);
                this.postInvalidate();
            }
        }

        return true;
    }


    /**
     * 滑动方法
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            mAnimationProvider.setTouchPoint(x,y);
            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y){
                isRunning = false;
            }
            postInvalidate();
        }
        super.computeScroll();
    }


    /**
     * 中止布局动画
     */
    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            mAnimationProvider.setTouchPoint(mScroller.getFinalX(),mScroller.getFinalY());
            postInvalidate();
        }
    }


    /**
     * 动画是否进行中
     * @return
     */
    public boolean isRunning(){
        return isRunning;
    }

    /**
     * 设置触摸监听
     * @param mTouchListener
     */
    public void setTouchListener(TouchListener mTouchListener){
        this.mTouchListener = mTouchListener;
    }

    /**
     * 触摸监听接口
     */
    public interface TouchListener{
        void center();
        Boolean prePage();
        Boolean nextPage();
        void cancel();
    }

}
