package com.key.magicbook.activity.options

import android.app.Activity
import com.key.keylibrary.base.BasePresenter

/**
 * created by key  on 2020/2/27
 */
class OptionPickViewPresenter : BasePresenter<Activity>() {
    override fun getView(): OptionsPickViewActivity {
        return iView as OptionsPickViewActivity
    }
}