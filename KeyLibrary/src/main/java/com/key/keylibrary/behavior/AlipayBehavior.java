package com.key.keylibrary.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.key.keylibrary.R;
import com.key.keylibrary.utils.UiUtils;

/**
 * @author HPT
 * @description:
 * @date : 2020/2/12 11:21
 */
public class AlipayBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    private Toolbar toolbar;

    /**
     * 下标
     */
    private int mPosition = -1;
    /**
     * X轴坐标
     */
    private float mViewMaxX = 0;

    /**
     * View的宽度
     */
    private int mViewWidth;
    /**
     * View的高度
     */
    private int mViewHeight;

    /**
     * Y轴的最大高度
     */
    private int mViewMaxY = 0;
    private float mfactor = 0;

    public void reset() {
        mPosition = -1;
        mViewMaxX = 0;
        mViewMaxY = 0;
        mfactor = 0;

    }


    public AlipayBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull LinearLayout child, @NonNull View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return false;


    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        if (mPosition == -1) {//未初始化
            toolbar = parent.findViewById(R.id.toolbar);
            mPosition = Integer.parseInt((String) child.getTag());
            //计算出每个View的宽度
            mViewWidth = dependency.getWidth() / 3;
            //高度
            mViewHeight = child.getHeight();
            mfactor = (float) toolbar.getHeight() / mViewHeight; //最大值和最小值
            //重新规划 宽度
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = mViewWidth;
            }
            child.setLayoutParams(layoutParams);

            // 总宽度 除以四。得出每一个子View的宽度，居中为
            //计算居中X轴
            mViewMaxX = mViewWidth * mPosition;
            //计算Y轴
            mViewMaxY = dependency.getHeight() / 2 - mViewHeight / 2;
            Log.e("aa", mViewMaxY + "");
        }

        //计算百分比  当前的百分比其实是没有减去状态栏的
        float mPercent = dependency.getY() / (dependency.getHeight() - toolbar.getHeight() - 50);
        if (mPercent >= 1f)
            mPercent = 1;

        // 动态更改 View的高度
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = (int) (mViewHeight - mViewHeight * (1 - mfactor) * mPercent);
            layoutParams.width = (int) (mViewWidth - mViewHeight * (1 - mfactor) * mPercent);
            child.setLayoutParams(layoutParams);
        }

        //更改 内部文字的透明底
        View mTextTitleView = child.getChildAt(1);
        if (mTextTitleView != null) {
            mTextTitleView.setAlpha(1 - (mPercent > 0.5 ? 1 : mPercent));
        }
//        if (toolbar != null)
 //           toolbar.findViewById(R.id.btn_right).setAlpha(mPercent < 0.5 ? 0 : mPercent);

        // 更改内部imageView的大小
        View mImageTitleView = child.getChildAt(0);
        if (mImageTitleView != null) {
            mImageTitleView.setScaleX(1 - ((1 - mfactor) * mPercent));
            mImageTitleView.setScaleY(1 - ((1 - mfactor) * mPercent));
        }


        //设置X轴坐标//没有计算状态栏的情况之下，滑动并不是完整的
        int x = (int) (dependency.getWidth() / 2 - mViewMaxX / 2);
        child.setX((float) (x + dependency.getWidth() / 2 * mPercent * 0.9));


        // 设置Y轴坐标
        child.setY(mViewMaxY - mViewMaxY * mPercent + UiUtils.getStateBar(parent.getContext()));


        return true;
    }
}
