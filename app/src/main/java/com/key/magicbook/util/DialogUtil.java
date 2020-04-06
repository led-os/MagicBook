package com.key.magicbook.util;

import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.key.keylibrary.utils.UiUtils;
import com.key.keylibrary.widget.CustomAlertDialog;
import com.key.magicbook.R;
import com.key.magicbook.base.RemindDialogClickListener;

/**
 * created by key  on 2020/4/6
 */
public class DialogUtil {


    public static AlertDialog getRemindDialog(Context context,
                                              String content,
                                              String confirm,
                                              String cancel,
                                              boolean isCancel,
                                              RemindDialogClickListener listener){
        AlertDialog dialog = new CustomAlertDialog().new
                Builder(context, UiUtils.inflate(context, R.layout.dialog_remind)).build();
        TextView tv_content =  dialog.findViewById(R.id.remind_content);
        TextView tv_cancel =  dialog.findViewById(R.id.cancel);
        TextView tv_confirm =  dialog.findViewById(R.id.confirm);
        tv_content.setText(content);
        tv_confirm.setText(confirm);
        tv_cancel.setText(cancel);


        if(listener!= null){
            tv_confirm.setOnClickListener(v -> listener.onClick(true));

            tv_cancel.setOnClickListener(v ->{
                listener.onClick(false);
                if(isCancel){
                    dialog.dismiss();
                }
            } );
        }
        return dialog;
    }
    public static AlertDialog getRemindDialog(Context context,String content,RemindDialogClickListener listener){
        return getRemindDialog(context,  content,"确定","取消",true,listener);
    }

    public static AlertDialog getRemindDialog(Context context,String content,boolean isCancel, RemindDialogClickListener listener){
        return getRemindDialog(context, content,"确定","取消",isCancel,listener);
    }


    public static AlertDialog getRemindDialog(Context context,String content,
                                              String confirm,boolean isCancel, RemindDialogClickListener listener,boolean isConfirm){
        if(isConfirm){
            return getRemindDialog(context,  content,confirm,"取消",isCancel,listener);
        }else{
            return getRemindDialog(context,  content,"确定",confirm,isCancel,listener);
        }

    }



}
