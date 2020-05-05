package com.key.magicbook.activity.login;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.allen.library.utils.ToastUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.key.keylibrary.widget.CustomEditTextView;
import com.key.magicbook.R;
import com.key.magicbook.base.RemindDialogClickListener;
import com.key.magicbook.bean.UserInfo;
import com.key.magicbook.util.DialogUtil;

import org.apache.commons.codec.digest.Md5Crypt;
import org.litepal.LitePal;

import java.util.List;

/**
 * created by key  on 2020/5/2
 */
public class RegisterBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private OnDismissListener onDismissListener;
    private OnCommitListener onCommitListener;
    private CustomEditTextView mAccount;
    private CustomEditTextView mPassword;
    private CustomEditTextView mConfirmPassword;
    private CustomEditTextView mName;

    private String lastAccount = "";
    private String lastName = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_register, null);
        initView(view);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }
        });
        return bottomSheetDialog;
    }

    private void initView(View view) {

        mAccount = view.findViewById(R.id.register_account);
        mPassword = view.findViewById(R.id.register_password);
        mPassword.setInputTypePass();
        mConfirmPassword = view.findViewById(R.id.register_confirm_password);
        mConfirmPassword.setInputTypePass();
        mName = view.findViewById(R.id.register_name);



        mAccount.setMineFocusChangeListener(new CustomEditTextView.MineFocusChangeListener() {
            @Override
            public void onFocus(boolean focus) {
                if(!lastAccount.equals(mAccount.getEditTextString())){
                    boolean empty = mAccount.getEditTextString().isEmpty();
                    if(!empty){
                        if(isRepetitionAccount(mAccount.getEditTextString())){
                            hintKeyBoard();
                            DialogUtil.getWarm(getActivity(), "此账号已经注册过了", new RemindDialogClickListener() {
                                @Override
                                public void onClick(boolean isConfirm) {

                                }
                            }).show();
                        }
                    }
                    lastAccount = mAccount.getEditTextString();
                }
            }
        });


        mName.setMineFocusChangeListener(new CustomEditTextView.MineFocusChangeListener() {
            @Override
            public void onFocus(boolean focus) {
                if(!lastName.equals(mName.getEditTextString())){
                    boolean empty = mName.getEditTextString().isEmpty();
                    if(!empty){
                        if(isRepetitionAccount(mName.getEditTextString())){

                            hintKeyBoard();
                            DialogUtil.getWarm(getActivity(), "此昵称已被其他人使用", new RemindDialogClickListener() {
                                @Override
                                public void onClick(boolean isConfirm) {

                                }
                            }).show();
                        }
                    }
                    lastName = mName.getEditTextString();
                }
            }
        });


        view.findViewById(R.id.commit)
                .setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        boolean check = false;
                        if(mAccount.getEditTextString().isEmpty()){
                            ToastUtils.showToast("请输入正确的账号");
                            check = true;
                        }else{
                            if(isRepetitionAccount(mAccount.getEditTextString())){
                                ToastUtils.showToast("此账号已经注册过了");
                                check = true;
                            }
                        }
                        if(mPassword.getEditTextString().isEmpty() && !check){
                            ToastUtils.showToast("请输入密码");
                            check = true;
                        }

                        if(mConfirmPassword.getEditTextString().isEmpty() && !check){
                            ToastUtils.showToast("请输入确认密码");
                            check = true;
                        }
                        if(!mConfirmPassword.getEditTextString().equals(mPassword.getEditTextString()) && !check){
                            ToastUtils.showToast("两次密码输入不一致");
                            check = true;
                        }

                        if(mName.getEditTextString().isEmpty() && !check){
                            ToastUtils.showToast("请输入昵称");
                            check = true;
                        }else{
                            if(isRepetitionName(mName.getEditTextString())){
                                ToastUtils.showToast("此昵称已被其他人使用");
                                check = true;
                            }
                        }
                        if(!check){
                            UserInfo userInfo = new UserInfo();
                            userInfo.setAccount(mAccount.getEditTextString());
                            userInfo.setPassword(Md5Crypt.apr1Crypt(mPassword.getEditTextString(),"key"));
                            userInfo.setUserName(mName.getEditTextString());
                            userInfo.setRealPassword(mPassword.getEditTextString());
                            userInfo.save();
                            ToastUtils.showToast("注册成功");
                            if(onCommitListener != null){
                                dismiss();
                                onCommitListener.onCommit(userInfo);
                            }
                        }




                    }
                });
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

    public interface OnDismissListener {
        public void onDismiss();
    }


    public interface OnCommitListener {
        public void onCommit(UserInfo userInfo);
    }


    public void setOnCommitListener(OnCommitListener onCommitListener){
        this.onCommitListener = onCommitListener;
    }


    private boolean isRepetitionAccount(String account){
        List<UserInfo> all =
                LitePal.findAll(UserInfo.class);
        for(UserInfo userInfo :all){
            boolean equals = userInfo.getAccount().equals(account);
            if(equals){
                return true;
            }
        }
        return false;
    }


    private boolean isRepetitionName(String name){
        List<UserInfo> all =
                LitePal.findAll(UserInfo.class);
        for(UserInfo userInfo :all){
            boolean equals = userInfo.getUserName().equals(name);
            if(equals){
                return true;
            }
        }
        return false;
    }


    private void hintKeyBoard(){
        View currentFocus = getActivity().getCurrentFocus();
        if(currentFocus != null){
            InputMethodManager systemService =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            systemService.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }


}
