package com.key.magicbook.base;


import android.os.Handler;

import com.allen.library.base.BaseObserver;
import com.allen.library.interfaces.ILoadingView;
import com.allen.library.utils.ToastUtils;

import io.reactivex.disposables.Disposable;

/**
 * created by key  on 2020/4/1
 */
public  abstract class CustomBaseObserver<T> extends BaseObserver<T>{
    private ILoadingView iLoadingView;
    public CustomBaseObserver(){

    }

    public CustomBaseObserver(ILoadingView iLoadingView){
        this.iLoadingView = iLoadingView;
    }
    @Override
    public void doOnSubscribe(Disposable d) {
        if(iLoadingView != null){
            iLoadingView.showLoadingView();
        }

    }

    @Override
    public void doOnError(String errorMsg) {
        if(iLoadingView != null){
            iLoadingView.hideLoadingView();
        }
    }

    @Override
    public void doOnNext(T o) {
           next(o);
    }

    @Override
    public void doOnCompleted() {
        if(iLoadingView != null){
            Handler handler = new Handler();
            handler.postDelayed(() ->
                    iLoadingView.hideLoadingView(), 50);

        }
    }


    public abstract void next(T o);



}
