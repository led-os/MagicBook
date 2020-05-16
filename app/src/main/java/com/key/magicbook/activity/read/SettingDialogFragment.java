package com.key.magicbook.activity.read;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.key.keylibrary.bean.BusMessage;
import com.key.keylibrary.widget.CircleImageView;
import com.key.magicbook.R;
import com.key.magicbook.activity.options.OptionsPickViewActivity;
import com.key.magicbook.bookpage.Config;
import com.key.magicbook.util.BrightnessUtil;
import com.key.magicbook.util.DisplayUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * created by key  on 2020/5/2
 */
public class SettingDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private OnDismissListener onDismissListener;
    private SettingListener mSettingListener;
    private Config config;
    private Boolean isSystem;
    private int FONT_SIZE_MIN;
    private int FONT_SIZE_MAX;
    private int currentFontSize;


    private CircleImageView bg_default;
    private CircleImageView bg_1;
    private CircleImageView bg_2;
    private CircleImageView bg_3;
    private CircleImageView bg_4;


    private TextView mSystemLight;
    private AppCompatSeekBar mSeekBarLight;

    private AppCompatSeekBar mSeekBarFont;
    private ConstraintLayout mFontType;
    private TextView mTypefaceShow;
    private ArrayList<Typeface> typefaces;
    private ArrayList<String> modeNames;

    private ConstraintLayout mPageModeSet;
    private TextView mPageModeShow;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_setting, null);
        initView(view);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }
        });

        return bottomSheetDialog;
    }

    private void initView(View view) {
        EventBus.getDefault().register(this);
        bg_default = view.findViewById(R.id.bg_default);
        bg_1 = view.findViewById(R.id.bg_1);
        bg_2 = view.findViewById(R.id.bg_2);
        bg_3 = view.findViewById(R.id.bg_3);
        bg_4 = view.findViewById(R.id.bg_4);

        mSystemLight = view.findViewById(R.id.system_light);
        mSeekBarLight = view.findViewById(R.id.seekBar_light);
        mSeekBarFont = view.findViewById(R.id.seekBar_font);

        mSystemLight.setOnClickListener(this);
        mSeekBarLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 10 && fromUser) {
                    changeBright(false, progress);
                    isSystem = false;
                    mSystemLight.setText("跟随系统");
                    mSystemLight.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_text_un_select));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        FONT_SIZE_MIN = (int) getContext().getResources().getDimension(R.dimen.reading_min_text_size);
        FONT_SIZE_MAX = (int) getContext().getResources().getDimension(R.dimen.reading_max_text_size);
        config = Config.getInstance();
        currentFontSize = (int) config.getFontSize();
        mSeekBarFont.setProgress(Integer.parseInt(currentFontSize+""));
        mSeekBarFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                config.setFontSize(progress);
                if(mSettingListener != null){
                    mSettingListener.changeFontSize(Integer.parseInt( progress+ ""));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        bg_default.setOnClickListener(this);
        bg_1.setOnClickListener(this);
        bg_2.setOnClickListener(this);
        bg_3.setOnClickListener(this);
        bg_4.setOnClickListener(this);


        isSystem = config.isSystemLight();

        float light = config.getLight();
        setBrightness(light);

        int bookBgType = config.getBookBgType();
        selectBg(bookBgType);


        Typeface defaultTypeface = config.getTypeface(Config.FONTTYPE_DEFAULT);
        Typeface qhTypeface = config.getTypeface(Config.FONTTYPE_QIHEI);
        Typeface ktTypeface = config.getTypeface(Config.FONTTYPE_FZKATONG);
        Typeface songTypeface = config.getTypeface(Config.FONTTYPE_BYSONG);
        typefaces = new ArrayList<>();
        typefaces.add(defaultTypeface);
        typefaces.add(qhTypeface);
        typefaces.add(ktTypeface);
        typefaces.add(songTypeface);
        String typefacePath = config.getTypefacePath();
        mFontType = view.findViewById(R.id.font_type);
        mTypefaceShow = view.findViewById(R.id.type_face_show);
        setTypeFace(typefacePath, -1);
        mFontType.setOnClickListener(v -> {
            int choosePosition = 0;
            switch (typefacePath) {
                case Config.FONTTYPE_DEFAULT:
                    choosePosition = 0;
                    break;
                case Config.FONTTYPE_QIHEI:
                    choosePosition = 1;
                    break;
                case Config.FONTTYPE_FZKATONG:
                    choosePosition = 2;
                    break;
                case Config.FONTTYPE_BYSONG:
                    choosePosition = 3;
                    break;
            }

            BusMessage<List<Typeface>> listBusMessage = new BusMessage<>();
            listBusMessage.setData(typefaces);
            listBusMessage.setMessage("typeface");
            listBusMessage.setTarget(OptionsPickViewActivity.class.getSimpleName());
            listBusMessage.setSpecialMessage(choosePosition + "");
            startActivity(new Intent(getActivity(), OptionsPickViewActivity.class));
            EventBus.getDefault().postSticky(listBusMessage);
        });


        mPageModeSet = view.findViewById(R.id.page_mode_set);
        mPageModeShow = view.findViewById(R.id.page_mode_show);
        modeNames = new ArrayList<String>();
        modeNames.add("仿真翻页");
        modeNames.add("覆盖翻页");
        modeNames.add("滑动翻页");
        modeNames.add("无");
        int pageMode = config.getPageMode();
        setPageMode(pageMode,-1);
        mPageModeSet.setOnClickListener(v -> {
            BusMessage<List<String>> listBusMessage = new BusMessage<>();
            listBusMessage.setData(modeNames);
            listBusMessage.setMessage("pageMode");
            listBusMessage.setTarget(OptionsPickViewActivity.class.getSimpleName());
            listBusMessage.setSpecialMessage(config.getPageMode() + "");
            startActivity(new Intent(getActivity(), OptionsPickViewActivity.class));
            EventBus.getDefault().postSticky(listBusMessage);
        });


    }

    private void setPageMode(int pageMode,int beChoose){
        int beChoosePosition = 0;
        if(beChoose == -1){
            switch (pageMode){
                case Config.PAGE_MODE_SIMULATION:
                    beChoosePosition = 0;
                    break;
                case Config.PAGE_MODE_COVER:
                    beChoosePosition = 1;
                    break;
                case Config.PAGE_MODE_SLIDE:
                    beChoosePosition = 2;
                    break;
                case Config.PAGE_MODE_NONE:
                    beChoosePosition = 3;
                    break;
            }
        }else{
            beChoosePosition = beChoose;
        }

        mPageModeShow.setText(modeNames.get(beChoosePosition));
        if(beChoose != -1){
            if(mSettingListener != null){
                config.setPageMode(beChoosePosition);
                mSettingListener.changePageMode(beChoosePosition);
            }
        }

    }

    private void setTypeFace(String typeFace, int beChoosePosition) {
        String beChooseTypeface = "";
        boolean change = false;
        if (beChoosePosition == -1) {
            switch (typeFace) {
                case Config.FONTTYPE_DEFAULT:
                    beChoosePosition = 0;
                    break;
                case Config.FONTTYPE_QIHEI:
                    beChoosePosition = 1;
                    break;
                case Config.FONTTYPE_FZKATONG:
                    beChoosePosition = 2;
                    break;
                case Config.FONTTYPE_BYSONG:
                    beChoosePosition = 3;
                    break;
                default:
                    change = false;
                    break;
            }
        }else{
            change = true;
        }

        String beChangeToTypeface = Config.FONTTYPE_DEFAULT;
        switch (beChoosePosition) {
            case 0:
                beChooseTypeface = "默认字体";
                beChangeToTypeface = Config.FONTTYPE_DEFAULT;
                break;
            case 1:
                beChooseTypeface = "旗黑字体";
                beChangeToTypeface = Config.FONTTYPE_QIHEI;
                break;
            case 2:
                beChooseTypeface = "卡通字体";
                beChangeToTypeface = Config.FONTTYPE_FZKATONG;
                break;
            case 3:
                beChooseTypeface = "宋体";
                beChangeToTypeface = Config.FONTTYPE_BYSONG;
                break;
        }
        mTypefaceShow.setText(beChooseTypeface);
        mTypefaceShow.setTypeface(typefaces.get(beChoosePosition));

        if(change){
            if(mSettingListener != null){
                config.setTypeface(beChangeToTypeface);
                mSettingListener.changeTypeFace(typefaces.get(beChoosePosition));
            }
        }

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    //设置字体
    public void setBookBg(int type) {
        config.setBookBg(type);
        if (mSettingListener != null) {
            mSettingListener.changeBookBg(type);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bg_default:
                setBookBg(Config.BOOK_BG_DEFAULT);
                selectBg(Config.BOOK_BG_DEFAULT);
                break;
            case R.id.bg_1:
                setBookBg(Config.BOOK_BG_1);
                selectBg(Config.BOOK_BG_1);
                break;
            case R.id.bg_2:
                setBookBg(Config.BOOK_BG_2);
                selectBg(Config.BOOK_BG_2);
                break;
            case R.id.bg_3:
                setBookBg(Config.BOOK_BG_3);
                selectBg(Config.BOOK_BG_3);
                break;
            case R.id.bg_4:
                setBookBg(Config.BOOK_BG_4);
                selectBg(Config.BOOK_BG_4);
                break;

            case R.id.system_light:
                isSystem = !isSystem;
                if (isSystem) {
                    changeBright(true, getSystemBrightness());
                    mSystemLight.setText("手动选择");
//                    mSystemLight.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_text_un_select));
                } else {
                    changeBright(false, mSeekBarLight.getProgress());
                    mSystemLight.setText("跟随系统");
//                    mSystemLight.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_text_select));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    public interface OnDismissListener {
        public void onDismiss();
    }


    public void setSettingListener(SettingListener settingListener) {
        this.mSettingListener = settingListener;
    }

    public interface SettingListener {
        void changeSystemBright(Boolean isSystem, float brightness);

        void changeFontSize(int fontSize);

        void changeTypeFace(Typeface typeface);

        void changeBookBg(int type);

        void changePageMode(int pageMode);
    }


    //选择背景
    private void selectBg(int type) {
        switch (type) {
            case Config.BOOK_BG_DEFAULT:
                bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                bg_1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case Config.BOOK_BG_1:
                bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_1.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                bg_2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case Config.BOOK_BG_2:
                bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_2.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                bg_3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case Config.BOOK_BG_3:
                bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_3.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                bg_4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case Config.BOOK_BG_4:
                bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                bg_4.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                break;
        }
    }





    //改变亮度
    public void changeBright(Boolean isSystem, int brightness) {
        float light = (float) (brightness / 100.0);
        config.setSystemLight(isSystem);
        config.setLight(light);
        if(isSystem){
            int screenBrightness = BrightnessUtil.getScreenBrightness(getActivity());
            mSeekBarLight.setProgress(screenBrightness);
            config.setLight(screenBrightness);
        }
        if (mSettingListener != null) {
            mSettingListener.changeSystemBright(isSystem, light);
        }
    }

    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    //设置亮度
    public void setBrightness(float brightness) {
        if(isSystem){
            mSystemLight.setText("手动选择");
        }else{
            mSeekBarLight.setProgress((int) (brightness * 100));
            mSystemLight.setText("跟随系统");
        }
    }


    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    public void onMessageReceive(BusMessage busMessage) {
        if (busMessage.getTarget() != null) {
            if (busMessage.getTarget().equals(SettingDialogFragment.class.getSimpleName())) {
                EventBus.getDefault().removeStickyEvent(busMessage);
                if(busMessage.getMessage().equals("typeface")){
                    getActivity().runOnUiThread(() -> {
                        Integer message = (Integer) busMessage.getData();
                        setTypeFace("", message);
                    });
                }else if(busMessage.getMessage().equals("pageMode")){
                    getActivity().runOnUiThread(() -> {
                        Integer message = (Integer) busMessage.getData();
                        setPageMode(0, message);
                    });
                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
