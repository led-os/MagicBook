package com.key.magicbook.activity.bookdetail

import com.key.magicbook.R
import com.key.magicbook.base.MineBaseActivity

/**
 * created by key  on 2020/4/13
 */
class BookDetailActivity :MineBaseActivity<BookDetailPresenter>(){
    override fun createPresenter(): BookDetailPresenter? {
        return BookDetailPresenter()
    }

    override fun initView() {

    }

    override fun setLayoutId(): Int {
       return R.layout.activity_book_detail
    }
}