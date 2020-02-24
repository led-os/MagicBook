package com.key.keylibrary.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.key.keylibrary.R;

/**
 * created by key  on 2019/11/13
 */
public class Toolbar extends LinearLayout {
    private Context context;
    private View mView;
    private TextView mLeftTitle;
    private TextView mCenterTitle;
    private ImageButton mBack;
    private LinearLayout mRightViewRoot;
    private AttributeSet attrs;
    private ConstraintLayout mRoot;
    private static final String backIcon = "back_icon";
    private static final String leftTitle = "left_title";
    private static final String centerTitle = "center_title";
    private static final String leftShow = "left_title_show";
    private static final String centerShow = "center_title_show";
    private static final String rightShow = "right_view_show";
    private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private OnBackClickListener onBackClickListener;
    public Toolbar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        init();
    }


    /**
     *   控件初始化
     */
    public void init(){
        mView =  inflate(context, R.layout.view_toolbar,this);
        mRoot = mView.findViewById(R.id.root);
        mLeftTitle =  mView.findViewById(R.id.left_title);
        mCenterTitle = mView.findViewById(R.id.center_title);
        mBack = mView.findViewById(R.id.back);
        mRightViewRoot = mView.findViewById(R.id.right_view);
        mBack.setOnClickListener(view -> {
            if(onBackClickListener != null){
                onBackClickListener.onBackClick();
            }
        });
        dealAttr();
    }
    /**
     *  控件属性初始化
     */
    public void dealAttr(){
        if(attrs != null){
            String mBackIcon = getAttributeValue(backIcon);
            if(mBackIcon != null){
                String trim = mBackIcon.replace("@", "").trim();
                mBack.setImageResource(Integer.valueOf(trim));
            }

            String mLeftShow = getAttributeValue(leftShow);
            if(mLeftShow != null){
                if(mLeftShow.equals("true")){
                    mLeftTitle.setVisibility(View.VISIBLE);
                }else{
                    mLeftTitle.setVisibility(View.GONE);
                }
            }

            String mLeftTitleString = getAttributeValue(leftTitle);
            if(mLeftTitleString != null){
                mLeftTitle.setText(mLeftTitleString);
            }


            String mCenterShow = getAttributeValue(centerShow);
            if(mCenterShow != null){
                if(mCenterShow.equals("true")){
                    mCenterTitle.setVisibility(View.VISIBLE);
                }else{
                    mCenterTitle.setVisibility(View.GONE);
                }
            }

            String mCenterTitleString = getAttributeValue(centerTitle);
            if(mCenterTitleString != null){
                mCenterTitle.setText(mCenterTitleString);
            }


            String mRightShow = getAttributeValue(rightShow);
            if(mRightShow != null){
                if(mRightShow.equals("true")){
                    mRightViewRoot.setVisibility(View.VISIBLE);
                }else{
                    mRightViewRoot.setVisibility(View.GONE);
                }
            }
        }
    }


    public void setBackIcon(int resourceId){
        if(mBack != null){
            mBack.setImageResource(resourceId);
        }
    }


    public void setLeftTitleShow(boolean show){
        if(mLeftTitle != null){
            if(show){
                mLeftTitle.setVisibility(View.VISIBLE);
            }else{
                mLeftTitle.setVisibility(View.GONE);
            }
        }
    }


    public void setCenterTitleShow(boolean show){
         if(mCenterTitle != null){
             if(show){
                 mCenterTitle.setVisibility(View.VISIBLE);
             }else{
                 mCenterTitle.setVisibility(View.GONE);
             }
         }
    }

    public void setRootBackgroundColor(int colorId,int selectorId){
        if(mRoot != null){
            mRoot.setBackgroundColor(getResources().getColor(colorId));
        }
        if(mBack != null){
            mBack.setBackgroundResource(selectorId);
        }
    }


    public void setLeftTitleText(String text){
        if(mLeftTitle != null){
            mLeftTitle.setText(text);
        }
    }


    public void setLeftTitleColor(int colorId){
        if(mLeftTitle != null){
            mLeftTitle.setTextColor(getResources().getColor(colorId));
        }
    }


    public void setCenterTitleText(String text){
        if(mCenterTitle != null){
            mCenterTitle.setText(text);
        }
    }


    public void setCenterTitleColor(int colorId){
        if(mCenterTitle != null){
            mCenterTitle.setTextColor(getResources().getColor(colorId));
        }
    }


    public LinearLayout getRightViewRoot() {
        return mRightViewRoot;
    }

    public String getAttributeValue(String attrName){
        String mLeftIcon = attrs.getAttributeValue(NAMESPACE, attrName);
        if(!TextUtils.isEmpty(mLeftIcon)){
           return mLeftIcon;
        }else{
            return null;
        }
    }

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }

    public interface OnBackClickListener{
         void onBackClick();
    }
}
