package com.key.magicbook.index

import com.key.keylibrary.base.BaseFragment
import com.key.magicbook.R
import kotlinx.android.synthetic.main.fragment_index_book_city.*

/**
 * created by key  on 2020/3/2
 */
class BookCityFragment :BaseFragment(){

    override fun setLayoutId(): Int {
        return R.layout.fragment_index_book_city
    }

    override fun initView() {
        setTitle(toolbar)
    }
}