package com.key.magicbook.activity.ranking

import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.magicbook.R
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.bean.BookRankBean
import com.key.magicbook.bean.BookRankContentBean
import kotlinx.android.synthetic.main.activity_ranking_list.*

/**
 * created by key  on 2020/4/15
 */
class RankingListActivity :MineBaseActivity<RankingListPresenter>() {
    private var bookRankBean : BookRankBean?= null
    private var contentAdapter:ContentAdapter ?= null
    private var menuAdapter:MenuAdapter ?= null
    private var bookRankContentBean :BookRankContentBean ?= null
    override fun createPresenter(): RankingListPresenter? {
        return RankingListPresenter()
    }

    override fun initView() {
        val contentLayoutManager = LinearLayoutManager(this)
        content.layoutManager = contentLayoutManager
        contentAdapter = ContentAdapter()
        content.adapter = contentAdapter

        val menuLayoutManager = LinearLayoutManager(this)
        menu.layoutManager = menuLayoutManager
        menuAdapter = MenuAdapter()
        menu.adapter = menuAdapter


        loadData(-1,true)
    }

    override fun setLayoutId(): Int {
       return R.layout.activity_ranking_list
    }


    public class MenuAdapter :BaseQuickAdapter<BookRankBean.BookRank,BaseViewHolder>(R.layout.item_menu){
        override fun convert(helper: BaseViewHolder, item: BookRankBean.BookRank) {

        }

    }


    public class ContentAdapter :BaseQuickAdapter<BookRankContentBean.BookRankContent,BaseViewHolder>(R.layout.activity_ranking_list){
        override fun convert(helper: BaseViewHolder, item: BookRankContentBean.BookRankContent) {

        }

    }
    private fun loadData(type :Int,refresh:Boolean) {

        if(type == -1){
            bookRankBean = BookRankBean()
            bookRankContentBean = BookRankContentBean()
        }else{
            loadView(bookRankBean,bookRankContentBean!!)
        }

    }

    private fun loadView(bookRankBean: BookRankBean?,bookRankContentBean: BookRankContentBean) {

        val ranks = bookRankBean!!.ranks
        val contents = bookRankContentBean!!.contents
        menuAdapter!!.setNewData(ranks)

        contentAdapter!!.setNewData(contents)
    }
}