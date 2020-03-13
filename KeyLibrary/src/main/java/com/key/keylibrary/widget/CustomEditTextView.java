package com.key.keylibrary.widget;

import android.annotation.SuppressLint;
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
import android.text.method.HideReturnsTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.key.keylibrary.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * created by key  on 2019/5/20
 */
public class CustomEditTextView extends ConstraintLayout {
    public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private Context context;
    private AttributeSet attrs;
    private ConstraintLayout mRoot;
    private TextView mHint;
    private EditText mEd;
    private String hint;
    private LinearLayout mNoClickView;
    private CheckListener checkListener;
    private ImageView delete_all;
    private boolean isCard = false;
    int lastContentLength = 0;
    boolean isDelete = false;
    private EditTextWatcherListener editTextWatcherListener;
    private MineFocusChangeListener mineFocusChangeListener;
    private String specialHint = "";
    private boolean checkNumber = false;
    private int mHintSize = 13;
    public interface CheckListener {
        void check(boolean check, EditText editText);
    }

    public CustomEditTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomEditTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public CustomEditTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        mRoot = (ConstraintLayout) inflate(context, R.layout.item_custom_edit, this);
        mHint = mRoot.findViewById(R.id.custom_remind);
        mEd = mRoot.findViewById(R.id.custom_edit);
        delete_all = mRoot.findViewById(R.id.delete_all);
        mNoClickView = mRoot.findViewById(R.id.cant_clickable);
        delete_all.setOnClickListener(v -> mEd.setText(""));
        mEd.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    disableShowInput(mEd);
                    break;
            }
            return false;
        });
        mEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isCard) {
                    StringBuffer sb = new StringBuffer(s);
                    isDelete = s.length() <= lastContentLength;
                    if (!isDelete && (s.length() == 4 || s.length() == 9 || s.length() == 14 || s.length() == 19)) {
                        if (s.length() == 4) {
                            sb.insert(4, " ");
                        } else if (s.length() == 9) {
                            sb.insert(9, " ");
                        } else if (s.length() == 14) {
                            sb.insert(14, " ");
                        } else if (s.length() == 19) {
                            sb.insert(19, " ");
                        }
                        setContent(sb);
                    }
                    lastContentLength = sb.length();
                }



            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    delete_all.setVisibility(View.GONE);
                } else {
                    delete_all.setVisibility(View.VISIBLE);
                    mHint.setVisibility(View.VISIBLE);
                    mEd.setHint("");
                }


                if (s.toString().length() == 2) {
                    String point = s.toString().substring(0, 1);
                    String number = s.toString().substring(1, 2);
                    if (point.equals(".")) {
                        Pattern p = Pattern.compile("[0-9]*");
                        Matcher m = p.matcher(number);
                        boolean matches = m.matches();
                        if (matches) {
                            Toast.makeText(context, "请输入正确的数值", Toast.LENGTH_SHORT).show();
                            mEd.setText("");
                        }
                    }
                    if(point.equals("0") && !number.equals(".") && checkNumber){
                        Toast.makeText(context, "请输入正确的数值", Toast.LENGTH_SHORT).show();
                        mEd.setText("");
                    }
                }

                if (editTextWatcherListener != null) {
                    editTextWatcherListener.finish();
                }

            }
        });

        String cg_hint = attrs.getAttributeValue(NAMESPACE, "cg_hint");
        if (cg_hint != null) {
            if (!cg_hint.isEmpty()) {
                if (cg_hint.contains("@")) {
                    String replace = cg_hint.replace("@", "");
                    hint = context.getResources().getString(Integer.valueOf(replace));
                    mHint.setText(hint);
                } else {
                    hint = cg_hint;
                    mHint.setText(cg_hint);
                }
            }
        }
        mEd.setOnFocusChangeListener((v, hasFocus) -> {
            if (mineFocusChangeListener != null) {
                mineFocusChangeListener.onFocus(hasFocus);
            }
            if (hasFocus) {
                if (!mEd.getText().toString().isEmpty()) {
                    delete_all.setVisibility(View.VISIBLE);
                } else {
                    delete_all.setVisibility(View.GONE);
                }
                mHint.setVisibility(View.VISIBLE);
                mEd.setHint("");
            } else {
                delete_all.setVisibility(View.GONE);
                String string = ((TextView) v).getText().toString();
                if (string.isEmpty()) {
                    illegal();
                } else {
                    mHint.setVisibility(View.VISIBLE);
                    if (checkListener != null) {
                        checkListener.check(false, mEd);
                    }
                }
            }
        });
    }


    public void setInputTypeChineseAndEnglish(boolean checkIsChinessAndEnglish) {
        InputFilter[] inputFilters = {new EditChineseAndEnglishInputFilter()};
        mEd.setFilters(inputFilters);
    }



    public void right() {
        if (mHint != null) {
            mHint.setVisibility(View.VISIBLE);
        }
    }

    public String getEditTextString() {
        return mEd.getText().toString();
    }


    public void setEditTextString(String str) {
        mEd.setText(str);
    }


    public void setHintString(String str) {
        mEd.setHint(str);
    }

    /**
     * 普通数字
     */
    public void setInputTypeNum() {
        mEd.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }




    /**
     * 邮箱
     */
    public void setInputTypeEmail() {
        mEd.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }


    /**
     * password
     */
    public void setInputTypePass() {
        mEd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void setInputTypeNumber() {
        mEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        mEd.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_NUMBER;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] ac = "0123456789".toCharArray();
                return ac;
            }
        });
    }
    public void setInputTypeNumberForFixed() {
        mEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        mEd.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_NUMBER;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] ac = "0123456789-".toCharArray();
                return ac;
            }
        });
    }


    public void setInputTypeNumberSp() {
        mEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        mEd.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_NUMBER;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] ac = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ<".toCharArray();
                return ac;
            }
        });
    }


    /**
     * 只能输入数字和字母
     */
    public void setInputTypeNormal() {
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            String speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            Pattern pattern = Pattern.compile(speChat);
            Matcher matcher = pattern.matcher(source.toString());
            if (matcher.find()) return "";
            else return null;
        };
        mEd.setFilters(new InputFilter[]{filter});
    }

    public void setEdHint(String str) {
        hint = str;
        mHint.setText(str);
        if (TextUtils.isEmpty(specialHint)) {
            mEd.setHint(str);
        } else {
            mEd.setHint(specialHint);
        }
    }

    public void setHintHide(){
        mHint.setVisibility(View.INVISIBLE);
    }


    public void setEdSpecialHint(String str) {
        specialHint = str;
    }

    public void setEdHintVisible() {
        mHint.setVisibility(View.VISIBLE);
    }

    public void illegal() {
        if (TextUtils.isEmpty(specialHint)) {
            mEd.setHint(hint);
        } else {
            mEd.setHint(specialHint);
        }
        mHint.setVisibility(View.INVISIBLE);
    }


    public void setCheckListener(CheckListener checkListener) {
        this.checkListener = checkListener;
    }


    public void setEditTextWatcherListener(EditTextWatcherListener editTextWatcherListener) {
        this.editTextWatcherListener = editTextWatcherListener;
    }


    public void setNoClickableShow() {
        mNoClickView.setVisibility(View.VISIBLE);
    }


    public void setMaxLength(int length) {
        mEd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }


    public void setCard(boolean card) {
        isCard = card;
    }


    private void setContent(StringBuffer sb) {
        mEd.setText(sb.toString());
        mEd.setSelection(sb.length());
    }

    public String getHint() {
        return hint;
    }

    public EditText getmEd() {
        return mEd;
    }


    public void disableShowInput(EditText et) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void setMineFocusChangeListener(MineFocusChangeListener mineFocusChangeListener) {
        this.mineFocusChangeListener = mineFocusChangeListener;
    }

    public interface MineFocusChangeListener {
        void onFocus(boolean focus);
    }

    public void setCheckNumber(boolean checkNumber){
        this.checkNumber = checkNumber;
    }


    public static class EditChineseAndEnglishInputFilter implements  InputFilter{
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if(TextUtils.isEmpty(source)){
                return "";
            }
            String string = source.toString();
            String substring = string.substring(string.length() - 1, string.length());
            String s = stringFilter(substring);
            if(!s.isEmpty()){
                return source;
            }else{
                source = string.substring(0,string.length() - 1);
            }
            return source;
        }
    }

    public static String stringFilter(String str)throws PatternSyntaxException {
        String regEx = "[^a-zA-Z\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public void setDeleteButtonShow(boolean show){
        if(show){
            delete_all.setVisibility(View.VISIBLE);
        }else{
            delete_all.setVisibility(View.INVISIBLE);
        }
    }


    public void setHintSizeAndColor(int color,int size){
        SpannableString ss = new SpannableString(hint);
        ss.setSpan(new ForegroundColorSpan(color), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size,true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mEd.setHint(new SpannedString(ss));
    }
    public interface EditTextWatcherListener {
        void finish();
    }
}
