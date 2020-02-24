package com.key.keylibrary.widget;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.key.keylibrary.R;
/**
 * created by key  on 2019/7/27
 */
public class EditTextView extends ConstraintLayout {
    public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private Context context;
    private AttributeSet attrs;
    private TextView mRemind;
    private EditText mContent;
    private ImageView mDelete;
    private ConstraintLayout mUnableClick;
    private String customHint = "";
    private String mEditClickable;
    private boolean clickable = true;
    private boolean isCard = false;
    private boolean isDelete = false;
    private int lastContentLength = 0;
    private int mHintSize = 13;
    public EditTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public EditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public EditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public void init(){
         ConstraintLayout mRoot = (ConstraintLayout) inflate(context, R.layout.edit_text_base, this);
         mRemind = mRoot.findViewById(R.id.ed_hint);
         mContent = mRoot.findViewById(R.id.ed_content);
         mDelete = mRoot.findViewById(R.id.delete_all);
         mUnableClick = mRoot.findViewById(R.id.unable_click);
         initListener();
         if(attrs != null){
             customHint = attrs.getAttributeValue(NAMESPACE, "edit_hint");
             if(customHint.contains("@")){
                 String hintId = customHint.replace("@", "").trim();
                 customHint = context.getResources().getString(Integer.valueOf(Integer.valueOf(hintId)));
             }
             mEditClickable = attrs.getAttributeValue(NAMESPACE, "edit_clickable");
             if(mEditClickable != null){
                 clickable = mEditClickable.equals("true");
                 if(!mEditClickable.equals("true")){
                     setUnableClick(true);
                 }else{
                     setUnableClick(false);
                 }
             }

             String edit_hint_size = attrs.getAttributeValue(NAMESPACE, "edit_hint_size");
             if(!TextUtils.isEmpty(edit_hint_size)){
                 mHintSize = Integer.valueOf(edit_hint_size);
                 setHintSizeAndColor(context.getResources().getColor(android.R.color.darker_gray));
             }

             String edit_type = attrs.getAttributeValue(NAMESPACE, "edit_type");
             if(!TextUtils.isEmpty(edit_type)){
                 String last = edit_type.substring(edit_type.length() - 1, edit_type.length());
                 switch (last){
                     case "0":{
                         Log.e("TYPE","0");
                         break;
                     }
                     case "1":{
                         Log.e("TYPE","1");
                         break;
                     }
                     case "2":{
                         Log.e("TYPE","2");
                         break;
                     }
                     case "3":{
                         Log.e("TYPE","3");
                         break;
                     }
                     case "4":{
                         Log.e("TYPE","4");
                         break;
                     }
                 }
             }
         }
         mRemind.setText(customHint);
         mRemind.setVisibility(View.INVISIBLE);
    }


    public void initListener(){
        mDelete.setOnClickListener(view -> mContent.setText(""));
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(isCard){
                    StringBuffer sb = new StringBuffer(s);
                    isDelete = s.length() <= lastContentLength;
                    if(!isDelete && (s.length() == 4||s.length() == 9 || s.length() == 14 || s.length() == 19)){
                        if(s.length() == 4) {
                            sb.insert(4, " ");
                        }else if(s.length() == 9) {
                            sb.insert(9, " ");
                        }else if(s.length() == 14){
                            sb.insert(14, " ");
                        }else if(s.length() == 19){
                            sb.insert(19, " ");
                        }
                        setContent(sb.toString());
                    }
                    lastContentLength = sb.length();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if(s.isEmpty()){
                    mDelete.setVisibility(View.GONE);
                }else{
                    if(clickable){
                        mDelete.setVisibility(View.VISIBLE);
                    }else{
                        mDelete.setVisibility(View.GONE);
                    }
                    mRemind.setVisibility(View.VISIBLE);
                    mContent.setHint("");
                }
            }
        });

        mContent.setOnFocusChangeListener((view, hasFocus) -> {
              if(hasFocus){
                  if(!mContent.getText().toString().isEmpty()){
                      mDelete.setVisibility(View.VISIBLE);
                  }else{
                      mDelete.setVisibility(View.GONE);
                  }
              }else{
                  mDelete.setVisibility(View.GONE);
                  String s = mContent.getText().toString();
                  if(s.isEmpty()){
                      illegal();
                  }else{

                  }
              }
        });
    }


    public void illegal(){
        mRemind.setVisibility(View.VISIBLE);
        setHintSizeAndColor(context.getResources().getColor(R.color.red));
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        mContent.startAnimation(shake);
        mRemind.setVisibility(View.INVISIBLE);
    }



    public void setCustomHint(String customHint){
        this.customHint = customHint;
        setHintSizeAndColor(context.getResources().getColor(R.color.red));
        mContent.setHintTextColor(getResources().getColor(R.color.red));
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        mContent.startAnimation(shake);
        mRemind.setVisibility(View.INVISIBLE);
    }

    public void setUnableClick(boolean isClick){
        if(isClick){
            mContent.setFocusable(false);
            mContent.setClickable(false);
            mContent.setFocusableInTouchMode(false);
            mUnableClick.setVisibility(View.VISIBLE);
        }else{
            mContent.setFocusable(true);
            mContent.setClickable(true);
            mUnableClick.setVisibility(View.GONE);
        }
    }



    /**
     * 普通数字可带小数点
     */
    public void setInputTypeNum() {
        mContent.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }
    /**
     * 电话号码
     */
    public void setInputTypePhone() {
        mContent.setInputType(InputType.TYPE_CLASS_PHONE);
    }


    /**
     * password
     */
    public void setInputTypePass() {
        mContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    /**
     * 只能输入数字和字母不带小数点
     *
     * @param isPass
     */
    public void setInputTypeNumberAndPass(boolean isPass) {
        mContent.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                if (isPass) {
                    return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                } else {
                    return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
                }

            }

            @Override
            protected char[] getAcceptedChars() {
                char[] ac = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                return ac;
            }
        });
    }

    public void setFilterString(String filterString){
        mContent.setKeyListener(new DigitsKeyListener(){
            @Override
            protected char[] getAcceptedChars() {
                char[] ac = filterString.toCharArray();
                return ac;
            }
        });
    }
    public String getEDString(){
        return  mContent.getText().toString();
    }

    public void setMaxLength(int length){ mContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)}); }

    public void setCard(boolean card) { isCard = card;}

    public String getHint(){
        return customHint;
    }

    public void setContent(String content){
        mContent.setText(content);
    }

    public void setHintSizeAndColor(int color){
        SpannableString ss = new SpannableString(customHint);
        ss.setSpan(new ForegroundColorSpan(color), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(mHintSize,true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mContent.setHint(new SpannedString(ss));
    }
}
