package com.key.magicbook.activity.set

import android.app.Activity
import com.key.keylibrary.base.BasePresenter

/**
 * created by key  on 2020/2/27
 */
class SetPresenter : BasePresenter<Activity>() {
    override fun getView(): SetActivity {
        return iView as SetActivity
    }
}