package com.key.keylibrary.utils;

import android.content.ClipboardManager;
import android.content.Context;

/**
 * created by key  on 2020/3/12
 */
public class getManagerUtil {


    /**
     *   获取粘贴板管理器
     * @param context
     * @return
     */
    public static ClipboardManager getClipboardManager(Context context){
       return  (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }
}
