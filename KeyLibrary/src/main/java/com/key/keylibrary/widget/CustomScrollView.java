package com.key.keylibrary.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * created by key  on 2019/9/19
 */
public class CustomScrollView extends ScrollView {
    private Context context;
    private ScrollChangeListener scrollChangeListener;
    public CustomScrollView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }



    public void init(){

    }


    /**
     *
     * @param l  目前的（滑动后）的X轴坐标
     * @param t  目前的（滑动后）的Y轴坐标
     * @param oldl  （滑动前）的X轴坐标
     * @param oldt  （滑动前）的Y轴坐标
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollChangeListener != null){
            scrollChangeListener.onScroll(l, t, oldl, oldt);
        }
    }


    public interface ScrollChangeListener{
        void onScroll(int l, int t, int oldl, int oldt);
    }


    public void setScrollChangeListener(ScrollChangeListener scrollChangeListener) {
        this.scrollChangeListener = scrollChangeListener;
    }


    /**
     *   不让scrollView自动切换子View的焦点
     * @param direction
     * @param previouslyFocusedRect
     * @return
     */
    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return true;
    }
}
