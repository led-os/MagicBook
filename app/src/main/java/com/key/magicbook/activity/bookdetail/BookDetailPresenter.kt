package com.key.magicbook.activity.bookdetail

import android.app.Activity
import com.key.keylibrary.base.BasePresenter

/**
 * created by key  on 2020/2/27
 */
class BookDetailPresenter : BasePresenter<Activity>() {
    override fun getView(): BookDetailActivity{
        return iView as BookDetailActivity
    }
}