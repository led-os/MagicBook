package com.key.magicbook.login

import android.app.Activity
import com.key.keylibrary.base.BasePresenter

/**
 * created by key  on 2020/2/27
 */
class LoginPresenter : BasePresenter<Activity>() {
    override fun getView(): LoginActivity {
        return iView as LoginActivity
    }
}