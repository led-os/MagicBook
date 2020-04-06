package com.key.magicbook.activity.splash

import android.app.Activity
import com.key.keylibrary.base.BasePresenter

/**
 * created by key  on 2020/1/5
 */
class SplashPresenter : BasePresenter<Activity>(),SplashContract.OnPresenter {




    override fun getView() :SplashActivity{
        return iView as SplashActivity
    }
}