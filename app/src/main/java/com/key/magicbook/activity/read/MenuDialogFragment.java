package com.key.magicbook.activity.read;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
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
public class MenuDialogFragment extends BottomSheetDialogFragment {
    private OnDismissListener onDismissListener;
    private RecyclerView mList;
    private Adapter adapter;
    private ArrayList<BookReadChapter> chapters;
    private String mBookOnlyTag = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        mBookOnlyTag = getArguments().getString("onlyTag");
    }

    public static MenuDialogFragment newInstance(String bookOnlyTag) {
        Bundle args = new Bundle();
        MenuDialogFragment fragment = new MenuDialogFragment();
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
        mList = view.findViewById(R.id.list);
        adapter = new Adapter();
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<BookReadChapter> bookReadChapters = LitePal.where("bookChapterOnlyTag = ?",
                mBookOnlyTag).order("chapterNum desc").find(BookReadChapter.class);
        mList.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view1, position) -> {

        });
        adapter.setNewData(bookReadChapters);
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


    public class Adapter extends BaseQuickAdapter<BookReadChapter, BaseViewHolder> {
        public Adapter() {
            super(R.layout.item_book_chapter);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, BookReadChapter bookReadChapter) {
            baseViewHolder.setText(R.id.chapter_name,bookReadChapter.getChapterName());
        }
    }
}
