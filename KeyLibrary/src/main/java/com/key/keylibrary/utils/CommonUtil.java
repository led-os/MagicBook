package com.key.keylibrary.utils;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import com.key.keylibrary.base.GlobalApplication;

/**
 * created by key  on 2020/3/13
 */
public class CommonUtil {
    /**
     *  是否有指纹识别
     * @param context
     * @return
     */
    public static boolean getFingerprintAvailable(Context context) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getApplicationContext()
                    .getSystemService(Context.FINGERPRINT_SERVICE);
            return !(fingerprintManager == null || !fingerprintManager.isHardwareDetected());
        }
        return false;
    }
}
