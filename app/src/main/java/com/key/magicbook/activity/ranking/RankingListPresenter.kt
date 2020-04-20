package com.key.magicbook.activity.ranking

import android.app.Activity
import com.key.keylibrary.base.BasePresenter

/**
 * created by key  on 2020/2/27
 */
class RankingListPresenter : BasePresenter<Activity>() {
    override fun getView(): RankingListActivity{
        return iView as RankingListActivity
    }
}