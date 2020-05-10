package com.key.magicbook.activity.index.booktype

import androidx.fragment.app.Fragment
import com.key.keylibrary.base.BasePresenter
import com.key.magicbook.activity.index.booktype.BookTypeFragment

/**
 * created by key  on 2020/5/10
 */
class BookTypePresenter  : BasePresenter<Fragment>() {
    override fun getView(): Fragment {
        return iView!!.get() as BookTypeFragment
    }
}