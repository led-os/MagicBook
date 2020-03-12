package com.key.magicbook.index

import android.app.Activity
import com.key.keylibrary.base.BasePresenter

/**
 * created by key  on 2020/2/27
 */
class IndexPresenter : BasePresenter<Activity>() {
    override fun getView(): IndexActivity {
        return iView as IndexActivity
    }
}