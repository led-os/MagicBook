package com.key.magicbook.search

import android.app.Activity
import com.key.keylibrary.base.BasePresenter

/**
 * created by key  on 2020/4/1
 */
class SearchPresenter : BasePresenter<Activity>() {
    override fun getView(): Activity {
        return iView as SearchActivity
    }
}