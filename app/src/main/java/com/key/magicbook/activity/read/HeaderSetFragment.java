package com.key.magicbook.activity.read;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.key.magicbook.R;
import com.key.magicbook.db.BookReadChapter;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * created by key  on 2020/5/2
 */
public class HeaderSetFragment extends BottomSheetDialogFragment {
    private OnDismissListener onDismissListener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    public static HeaderSetFragment newInstance(String bookOnlyTag) {
        Bundle args = new Bundle();
        HeaderSetFragment fragment = new HeaderSetFragment();
        args.putString("onlyTag",bookOnlyTag);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_menu, null);
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


    interface  OnMenuClickListener{
        public void onMenuClick(int currentPosition);
    }
}
