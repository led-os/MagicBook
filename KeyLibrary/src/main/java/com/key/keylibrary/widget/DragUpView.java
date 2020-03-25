package com.key.keylibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.key.keylibrary.R;

/**
 * created by key  on 2019/7/1
 */
public class DragUpView extends LinearLayout implements View.OnTouchListener , GestureDetector.OnGestureListener{
    private Context context;
    private AttributeSet attrs;
    private int defStyleAttr;
    private LinearLayout mRoot;
    private GestureDetector mGestureDetector;
    private View v;
    private Scroller scroller;
    private int lastLeft = 0;
    private int lastTop = 0;
    private int firstTop = 0;
    private ScorlllListener scorlllListener;
    private TapCliakListener tapCliakListener;
    public interface TapCliakListener{
        void tapClick();
    }

    private DismissListener dismissListener;
    public interface DismissListener{
        void dismiss();
    }
    public DragUpView(Context context) {
        super(context);
        this.context = context;
        scroller = new Scroller(context,new AccelerateInterpolator(),false);
        init();
    }

    public DragUpView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        scroller = new Scroller(context,new AccelerateInterpolator(),false);
        init();
    }

    public DragUpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
        scroller = new Scroller(context,new AccelerateInterpolator(),false);
        init();
    }


    public void init(){
        View inflate = inflate(context, R.layout.item_drag_up, this);
        mRoot = inflate.findViewById(R.id.root);
        mRoot.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(context,this);
        mRoot.setClickable(true);
        mRoot.setFocusable(true);
        mRoot.setLongClickable(true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.v = v;
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:

                if(scorlllListener != null){
                    scorlllListener.onScrollUp();
                }
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if(tapCliakListener != null){
            tapCliakListener.tapClick();
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float scrollX = e2.getX() - e1.getX();
        float scrollY = e2.getY() - e1.getY();
        int x = floatToInteger(scrollX);
        int y = floatToInteger(scrollY);
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom() + y;
        int top = v.getTop() + y;
        v.layout(left,top,right,bottom);
        lastLeft = left;
        lastTop = top;
        if(scorlllListener != null){
            scorlllListener.onScroll(left,top);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        return false;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            ((View)v.getParent()).scrollTo(scroller.getCurrX(),scroller.getCurrY());
            postInvalidate();
        }
    }
    private void startScroll(int currentX,int destX){
        //这里设置从0,0 开始，x、y1000毫秒内分别向左和上偏移200个单位
        //可以自己动态控制四个值
        scroller.startScroll(currentX,0,destX,0,1000);
        //记得刷新界面
        invalidate();
    }

    public void setTapCliakListener(TapCliakListener tapCliakListener) {
        this.tapCliakListener = tapCliakListener;
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }


    public  int floatToInteger(Float f){
        String number = String.valueOf(f);
        if(number.isEmpty()){
            return 0;
        }else{
            int i = number.indexOf(".");
            String substring = number.substring(0, i);
            if(Integer.valueOf(number.substring(i+1,i+2)) > 5){
                return Integer.valueOf(substring)+1;
            }else{
                return Integer.valueOf(substring);
            }

        }
    }

    public interface ScorlllListener{
        void onScroll(int x, int y);
        void onScrollUp();
    }


    public void setScorlllListener(ScorlllListener scorlllListener) {
        this.scorlllListener = scorlllListener;
    }
}
