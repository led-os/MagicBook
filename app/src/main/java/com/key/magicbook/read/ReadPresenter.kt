package com.key.magicbook.read

import android.app.Activity
import com.key.keylibrary.base.BasePresenter

/**
 * created by key  on 2020/2/27
 */
class ReadPresenter : BasePresenter<Activity>() {
    override fun getView(): ReadActivity {
        return iView as ReadActivity
    }
}