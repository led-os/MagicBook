package com.key.magicbook.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.key.keylibrary.utils.UiUtils;
import com.key.keylibrary.widget.CircleImageView;
import com.key.magicbook.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * created by key  on 2020/5/15
 */
public class HomeRefreshHeader extends LinearLayout  implements RefreshHeader {
    private ConstraintLayout mImageRoot;
    private CircleImageView mImageCenter;
    private CircleImageView mImageLeft;
    private CircleImageView mImageRight;
    private int mImgCenterSize = 0;

    public HomeRefreshHeader(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_home_refresh, this);

        mImageRoot = findViewById(R.id.image_root);
        mImageCenter = findViewById(R.id.img_center);
        mImageLeft = findViewById(R.id.img_left);
        mImageRight = findViewById(R.id.img_right);
        mImgCenterSize = UiUtils.dip2px(30f);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return  SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

        if(isDragging){

            MarginLayoutParams layoutParams = (MarginLayoutParams) mImageRoot.getLayoutParams();
            int i = UiUtils.dip2px(height * percent) / 8;
            layoutParams.bottomMargin = i;
            mImageRoot.setLayoutParams(layoutParams);

            int height1 = mImageRoot.getHeight();

            MarginLayoutParams layoutParamsCenter = (MarginLayoutParams) mImageCenter.getLayoutParams();
            int i1 = (int) (mImgCenterSize * percent);
            if(height1 > i1){
                layoutParamsCenter.height = i1;
                layoutParamsCenter.width = i1;
            }

            mImageCenter.setLayoutParams(layoutParamsCenter);

            MarginLayoutParams layoutParamsLeft = (MarginLayoutParams) mImageLeft.getLayoutParams();
            int i2 = (int) (mImgCenterSize * percent);
            if((height1/3) * 2 > i2){
                layoutParamsLeft.height =i2;
                layoutParamsLeft.width = i2;
            }

            layoutParamsLeft.leftMargin = (int) (60 *percent);
            mImageLeft.setLayoutParams(layoutParamsLeft);


            MarginLayoutParams layoutParamsRight = (MarginLayoutParams) mImageRight.getLayoutParams();
            int i3 = (int) (mImgCenterSize * percent);
            if((height1/3) * 2  > i3){
                layoutParamsRight.height = i3;
                layoutParamsRight.width = i3;
            }

            layoutParamsRight.rightMargin = (int) (60 *percent);
            mImageRight.setLayoutParams(layoutParamsRight);

        }

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        return 0;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout,
                               @NonNull RefreshState oldState, @NonNull RefreshState newState) {

    }
}
