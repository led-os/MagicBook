package com.key.magicbook.activity.index

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.base.BaseFragment
import com.key.magicbook.R
import com.key.magicbook.activity.read.ReadActivity
import kotlinx.android.synthetic.main.fragment_index_mine_book.*


/**
 * created by key  on 2020/3/2
 */
class BookRackFragment :BaseFragment(){
    override fun bindView() {

    }

    override fun setLayoutId(): Int {
        return  R.layout.fragment_index_mine_book
    }

    companion object {
        fun  newInstance():BookRackFragment{
            val bookRackFragment = BookRackFragment()
            val bundle = Bundle()
            bookRackFragment.arguments = bundle
            return bookRackFragment
        }
    }
    override fun initView() {
        setTitle(refresh)
        refresh.setEnableRefresh(false)
        refresh.setEnableLoadMore(false)


        val layoutManager = GridLayoutManager(activity, 3)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        list.layoutManager = layoutManager


        val mineBookAdapter = MineBookAdapter(R.layout.item_book_mine)
        list.adapter = mineBookAdapter

        val arrayList = ArrayList<String>()
        arrayList.add("圣墟")



        mineBookAdapter.setNewData(arrayList)



        mineBookAdapter.setOnItemClickListener { _,
                                                 _,
                                                 _ ->
            startActivity(Intent(activity,ReadActivity::class.java))
            activity!!.overridePendingTransition(0,0)
        }
    }


    inner class MineBookAdapter(res :Int) :BaseQuickAdapter<String,BaseViewHolder>(res){
        override fun convert(helper: BaseViewHolder, item: String) {
            helper.setText(R.id.book_name,item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments
    }
}




