package com.key.magicbook.base;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import com.allen.library.interfaces.ILoadingView;
import com.key.keylibrary.utils.UiUtils;
import com.key.keylibrary.widget.CustomAlertDialog;
import com.key.magicbook.R;
import com.tamsiree.rxui.view.progressing.style.CubeGrid;

/**
 * created by key  on 2020/4/2
 */
public class LoadingView implements ILoadingView {
    private CubeGrid cubeGrid;
    private AlertDialog loadingDialog;
    @Override
    public void showLoadingView() {
        if(loadingDialog != null){
            loadingDialog.show();
            if(cubeGrid != null){
                cubeGrid.start();
            }

        }

    }

    @Override
    public void hideLoadingView() {
        if(loadingDialog != null){
            loadingDialog.hide();
            if(cubeGrid != null){
                cubeGrid.stop();
            }
        }
    }



    public LoadingView(Context context){
        View inflate = UiUtils.inflate(context, R.layout.item_loading);
        ImageView imageView = inflate.findViewById(R.id.image);
        cubeGrid = new CubeGrid();
        cubeGrid.setColor(context.getResources().getColor(R.color.white));
        imageView.setImageDrawable(cubeGrid);
        loadingDialog = new CustomAlertDialog().
                new Builder(context, inflate)
                .setWidth(WindowManager.LayoutParams.WRAP_CONTENT)
                .setVerticalPadding(0)
                .setHorizontalPadding(0)
                .build();
    }
}
