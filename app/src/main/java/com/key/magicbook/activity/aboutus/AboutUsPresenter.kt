package com.key.magicbook.activity.set

import android.app.Activity
import com.key.keylibrary.base.BasePresenter
import com.key.magicbook.activity.history.HistoryActivity
import com.key.magicbook.activity.like.LikeActivity

/**
 * created by key  on 2020/2/27
 */
class AboutUsPresenter : BasePresenter<Activity>() {
    override fun getView(): HistoryActivity {
        return iView as HistoryActivity
    }
}