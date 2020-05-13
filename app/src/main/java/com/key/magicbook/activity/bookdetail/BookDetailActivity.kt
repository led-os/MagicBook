package com.key.magicbook.activity.bookdetail

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.allen.library.interceptor.Transformer
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.bean.BusMessage
import com.key.magicbook.R
import com.key.magicbook.activity.read.ReadActivity
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.db.BookDetail
import com.key.magicbook.db.BookLike
import com.key.magicbook.db.BookReadChapter
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.GlideUtils
import com.wx.goodview.GoodView
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.fragment_index_mine.toolbar
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.litepal.LitePal

/**
 * created by key  on 2020/4/13
 */
class BookDetailActivity : MineBaseActivity<BookDetailPresenter>() {
    private var localChapterUrls: ArrayList<String>? = null
    private var bookName = ""
    private var adapter:Adapter ?= null
    private var mBookDetail : BookDetail?=null
    private var goodView:GoodView ?= null
    private var isLike = false
    private var bookUrl = ""
    override fun createPresenter(): BookDetailPresenter? {
        return BookDetailPresenter()
    }

    override fun initView() {
        setTitle(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
        list.layoutManager = LinearLayoutManager(this)
        adapter = Adapter()
        adapter!!.setOnItemClickListener { _,
                                           _,
                                           position ->
            presenter!!.getBookContent( ConstantValues.BASE_URL + localChapterUrls!![position],position)
        }
        list.adapter = adapter
        cache.setOnClickListener {

        }
        add.setOnClickListener {

        }
        read.setOnClickListener {
            checkRead()
        }
        goodView= GoodView(this)
        like.setOnClickListener {
            var imgId = R.mipmap.book_like
            if(isLike){
                imgId = R.mipmap.book_un_like
            }
            Glide.with(this).load(imgId).into(like)
            isLike = !isLike
            goodView!!.setImage(imgId)
            goodView!!.show(like)

            val bookLike = BookLike()
            bookLike.bookName = mBookDetail!!.bookName
            bookLike.bookAuthor = mBookDetail!!.bookAuthor
            bookLike.bookUrl = bookUrl
            bookLike.bookOnlyTag = mBookDetail!!.bookName +  mBookDetail!!.bookAuthor + bookUrl
            if(isLike){
                val contentValues = ContentValues()
                contentValues.put("isLike", "true")
                LitePal.updateAll(BookLike::class.java,contentValues,"bookOnlyTag = ?",bookLike.bookOnlyTag)
            }else{
                val contentValues = ContentValues()
                contentValues.put("isLike", "false")
                LitePal.updateAll(BookLike::class.java,contentValues,"bookOnlyTag = ?",bookLike.bookOnlyTag)
            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_book_detail
    }

    override fun receiveMessage(busMessage: BusMessage<Any>) {
        super.receiveMessage(busMessage)
        bookName = busMessage.specialMessage
        bookUrl = busMessage.message
        if(busMessage.data != null){
            runOnUiThread {
                executeData(busMessage.data as Document)
            }
        }else{
            Log.e("pile",bookUrl)
        }
    }

    private fun executeData(document: Document) {
        mBookDetail = presenter!!.parseBookDetail(document, bookUrl)
        presenter!!.getChapters(mBookDetail!!)
    }

     fun loadView(bookDetail: BookDetail) {
        toolbar.title = bookDetail.bookName
        localChapterUrls = bookDetail.chapterUrls as ArrayList<String>?
        var bookAuthor = bookDetail.bookAuthor
        if(bookAuthor.contains("：")){
            bookAuthor = bookAuthor.split("：")[1]
        }
        author.text = bookAuthor
        var lastUpdateTime = bookDetail.lastUpdateTime
        if(lastUpdateTime.contains("：")){
            lastUpdateTime = lastUpdateTime.split("：")[1]
        }
        last_update_info_time.text = lastUpdateTime
        last_update_info_chapter.text = bookDetail.lastChapter
        intro.text = bookDetail.bookIntro
        adapter!!.setNewData(bookDetail.chapterNames)
        GlideUtils.load(this,bookDetail.bookCover,book_cover)
        Thread {
            GlideUtils.loadBlur(this,bookDetail.bookCover,book_root)
        }.start()


        var isCheck = false
        val findAll = LitePal.findAll(BookLike::class.java)
        val tag = bookName + bookDetail.bookAuthor + bookUrl
        for(value in findAll){
            if(tag == value.bookOnlyTag){
                isCheck = true
                isLike = value.isLike == "true"
            }
        }
        var imgId =  R.mipmap.book_un_like
        if(isLike){
           imgId =  R.mipmap.book_like
        }
        if(!isCheck){
            val bookLike = BookLike()
            bookLike.bookName = bookName
            bookLike.bookAuthor = mBookDetail!!.bookAuthor
            bookLike.bookUrl = bookUrl
            bookLike.bookOnlyTag = bookName + mBookDetail!!.bookAuthor + bookUrl
            bookLike.isLike  = "false"
            bookLike.save()
        }

        Glide.with(this).load(imgId).into(like)
    }


    override fun onResume() {
        super.onResume()
        toolbar.title = bookName
    }

    class Adapter() :BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_chapter){
        override fun convert(helper: BaseViewHolder, item: String) {
             helper.setText(R.id.chapter,item)
        }
    }


    fun bookContent(content :String,chapterPosition: Int){
        val busMessage = BusMessage<BookDetail>()
        busMessage.data = mBookDetail
        busMessage.message = content
        busMessage.target = ReadActivity::class.java.simpleName
        busMessage.specialMessage = chapterPosition.toString()
        sendBusMessage(busMessage = busMessage)
        startActivity(Intent(this@BookDetailActivity, ReadActivity::class.java))
    }

    private fun checkRead(){
        val find = LitePal.where(
            "bookChapterOnlyTag = ? and isLook = ? ",
            mBookDetail!!.bookName + mBookDetail!!.bookAuthor + mBookDetail!!.bookUrl, "true"
        ).find(BookReadChapter::class.java)

        if(find.size == 0){
            if(localChapterUrls!!.size - 1 >= 0 ){
                val bookUrl = ConstantValues.BASE_URL + localChapterUrls!![0]
                presenter!!.getBookContent(bookUrl,0)
            }
        }else{
            val bookReadChapter = find[0]
            val busMessage = BusMessage<BookDetail>()
            busMessage.data = mBookDetail
            busMessage.message = bookReadChapter.bookChapterContent
            busMessage.target = ReadActivity::class.java.simpleName
            busMessage.specialMessage = bookReadChapter.chapterNum.toString()
            sendBusMessage(busMessage = busMessage)
            startActivity(Intent(this@BookDetailActivity,ReadActivity::class.java))
        }
    }
}