package com.key.magicbook.activity.history

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.read.ReadActivity
import com.key.magicbook.activity.set.HistoryPresenter
import com.key.magicbook.activity.set.LikePresenter
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.db.BookDetail
import com.key.magicbook.db.BookLike
import com.key.magicbook.helper.WeSwipe
import com.key.magicbook.helper.WeSwipeHelper
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.GlideUtils
import kotlinx.android.synthetic.main.activity_like.*
import kotlinx.android.synthetic.main.activity_set.*
import kotlinx.android.synthetic.main.activity_set.toolbar
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.litepal.LitePal

/**
 * created by key  on 2020/3/25
 */
class HistoryActivity : MineBaseActivity<HistoryPresenter>() {
    private var adapter:Adapter ?= null
    private var likes :ArrayList<BookDetail> ?= null
    override fun createPresenter(): HistoryPresenter {
        return HistoryPresenter()
    }

    override fun initView() {
        setTitle(toolbar)
        initToolbar(toolbar)

        toolbar.title = "阅读历史"

        list.layoutManager = LinearLayoutManager(this)
        adapter = Adapter()
        list.adapter = adapter

        adapter!!.setEmptyView(UiUtils.inflate(this,R.layout.no_data))
        adapter!!.setOnItemClickListener { adapter,
                                           view, position ->
            val busMessage = BusMessage<BookDetail>()
            busMessage.data = adapter.data[position] as BookDetail
            busMessage.message = ""
            busMessage.target = ReadActivity::class.java.simpleName
            busMessage.specialMessage = "0"
            sendBusMessage(busMessage = busMessage)
            startActivity(Intent(this@HistoryActivity, ReadActivity::class.java))

        }

        reloadData()
        WeSwipe.attach(list)
    }


    private fun reloadData(){
        likes = ArrayList();
        val findAll = LitePal
            .where("userName = ?",getUserInfo().userName).find(BookLike::class.java)

        for(value in findAll){
            if(value.isLooked == "true" && value.bookOnlyTag != null && value.bookName != null){
                if(value.bookName.isNotEmpty()){
                    val bookDetail = getBookDetail(value)
                    if(bookDetail.bookName !=null){
                        if(bookDetail.bookName.isNotEmpty()){
                            likes!!.add(bookDetail)
                        }
                    }
                }
            }
        }


        adapter!!.setNewData(likes)
    }
    override fun setLayoutId(): Int {
        return R.layout.activity_like
    }


    inner class Adapter :BaseQuickAdapter<BookDetail,LikeViewHolder>(R.layout.item_like){

        override fun convert(helper: LikeViewHolder, item: BookDetail) {

            helper.setText(R.id.name, item.bookName)
            helper.setText(R.id.author, item.bookAuthor)
            GlideUtils.loadGif(context, helper.getView(R.id.img))
            GlideUtils.load(
                context,
                item.bookCover,
                helper.getView(R.id.img)
            )
            helper.setText(R.id.time, item.lastChapter + "\n" +item.lastUpdateTime)
            helper.getView<TextView>(R.id.slide).setOnClickListener {
                val contentValues = ContentValues()
                contentValues.put("isLooked","false")
                LitePal.updateAll(BookLike::class.java,contentValues,
                    "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?",
                    item.bookName, item.baseUrl, item.bookUrl,getUserInfo().userName
                )
                reloadData()
            }
        }
    }

     class LikeViewHolder(view: View) : BaseViewHolder(view), WeSwipeHelper.SwipeLayoutTypeCallBack{
        var slide: TextView? = null
        var contentView : View ?= null

         init {
             contentView = view.findViewById(R.id.content)
             slide = view.findViewById(R.id.slide)
         }
        override fun getSwipeWidth(): Float {
            return if(slide != null){
                slide!!.width.toFloat()
            }else{
                0f
            }

        }

        override fun onScreenView(): View? {
            return if(contentView != null){
                contentView!!
            }else{
                null
            }

        }

        override fun needSwipeLayout(): View ?{
            return if(contentView != null){
                contentView!!
            }else{
                null
            }
        }

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