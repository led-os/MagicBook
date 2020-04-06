package com.key.magicbook.activity.index

import android.content.Intent
import com.key.keylibrary.base.BaseFragment
import com.key.magicbook.R
import com.key.magicbook.activity.search.SearchActivity
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
        search.setOnClickListener {
            startActivity(Intent(activity,SearchActivity::class.java))
            activity!!.overridePendingTransition(0,0)
        }
    }
}