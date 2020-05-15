package com.key.magicbook.activity.index

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.base.BaseFragment
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.read.ReadActivity
import com.key.magicbook.bean.UserInfo
import com.key.magicbook.db.BookDetail
import com.key.magicbook.db.BookLike
import com.key.magicbook.util.GlideUtils
import kotlinx.android.synthetic.main.fragment_index_mine_book.*
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal


/**
 * created by key  on 2020/3/2
 */
class BookRackFragment : BaseFragment() {
    private var mineBookAdapter :MineBookAdapter ?= null
    override fun bindView() {}

    override fun setLayoutId(): Int {
        return R.layout.fragment_index_mine_book
    }

    companion object {
        fun newInstance(): BookRackFragment {
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


        mineBookAdapter = MineBookAdapter(R.layout.item_book_mine)
        list.adapter = mineBookAdapter
        mineBookAdapter!!.setEmptyView(UiUtils.inflate(activity,R.layout.no_data))

        mineBookAdapter!!.setOnItemClickListener { adapter,
                                                 _,
                                                 position ->

            val busMessage = BusMessage<BookDetail>()
            val item = adapter.getItem(position) as BookLike
            busMessage.data = getBookDetail(item)
            busMessage.message = ""
            busMessage.target = ReadActivity::class.java.simpleName
            busMessage.specialMessage = "0"
            EventBus.getDefault().postSticky(busMessage)
            startActivity(Intent(activity, ReadActivity::class.java))
            activity!!.overridePendingTransition(0, 0)
        }


        loadData();
    }

    private fun loadData() {

        val find =
            LitePal.where("isBookCase = ? and userName = ?", "true",getUserInfo().userName).find(BookLike::class.java)

        if(find.size > 0){
            mineBookAdapter!!.setNewData(find)
        }
    }

    inner class MineBookAdapter(res: Int) : BaseQuickAdapter<BookLike, BaseViewHolder>(res) {
        override fun convert(helper: BaseViewHolder, item: BookLike) {
            helper.setText(R.id.book_name, item.bookName)
            GlideUtils.load(activity,item.bookCover,helper.getView(R.id.book_cover))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments
    }

    private fun getUserInfo():UserInfo{
        val findAll = LitePal.findAll(UserInfo::class.java)
        for(value in findAll){
            if(value.isLogin == "true"){
                return value
            }
        }
        return UserInfo()
    }

    private fun getBookDetail(bookLike: BookLike):BookDetail{
        val find = LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ?",
            bookLike.bookName, bookLike.baseUrl, bookLike.bookUrl
        ).find(BookDetail::class.java)
        return if(find.size > 0){
            find[0];
        }else{
            BookDetail()
        }
    }
}




