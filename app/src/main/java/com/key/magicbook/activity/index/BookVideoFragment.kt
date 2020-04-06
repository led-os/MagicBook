package com.key.magicbook.activity.index

import com.key.keylibrary.base.BaseFragment
import com.key.magicbook.R
import kotlinx.android.synthetic.main.fragment_index_book_video.*

/**
 * created by key  on 2020/3/2
 */
class BookVideoFragment :BaseFragment(){

    override fun setLayoutId(): Int {
        return R.layout.fragment_index_book_video
    }

    override fun initView() {
        setTitle(toolbar)
    }
}