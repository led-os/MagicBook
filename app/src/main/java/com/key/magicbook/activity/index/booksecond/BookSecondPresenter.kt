package com.key.magicbook.activity.index.booksecond

import androidx.fragment.app.Fragment
import com.key.keylibrary.base.BasePresenter
import com.key.magicbook.activity.index.booktype.BookTypeFragment

/**
 * created by key  on 2020/5/10
 */
class BookSecondPresenter  : BasePresenter<Fragment>(),BookSecondContract.OnPresenter {
    private var model:BookSecondModel ?= null
    init {
        model = BookSecondModel()
    }
    override fun getView(): SecondFragment {
        return iView!!.get() as SecondFragment
    }

    override fun getBookUrls(): List<String> {
         return model!!.getBookUrls()
    }

    override fun getBookUrlsName(): List<String> {
         return model!!.getBookUrlsName()
    }
}