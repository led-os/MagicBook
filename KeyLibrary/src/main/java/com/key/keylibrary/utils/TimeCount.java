package com.key.keylibrary.utils;

import android.os.CountDownTimer;
import android.widget.Button;

import com.key.keylibrary.R;


public class TimeCount extends CountDownTimer {
    Button btnGetcode;
    public TimeCount(long millisInFuture, long countDownInterval, Button btnGetcode) {
        super(millisInFuture, countDownInterval);
        this.btnGetcode=btnGetcode;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        //btnGetcode.setBackgroundColor(R.drawable.selector_btn_gray);
        btnGetcode.setBackgroundResource(R.drawable.selector_btn_gray);
        //btnGetcode.setClickable(false);
        btnGetcode.setEnabled(false);
        btnGetcode.setText("" + millisUntilFinished / 1000 + " 秒重发");
    }

    @Override
    public void onFinish() {
        btnGetcode.setText("获取验证码");
        btnGetcode.setEnabled(true);
        btnGetcode.setBackgroundResource(R.drawable.selector_btn_green);
    }

}