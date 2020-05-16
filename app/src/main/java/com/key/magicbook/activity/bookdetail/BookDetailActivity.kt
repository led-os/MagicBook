package com.key.magicbook.activity.bookdetail

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.bean.BusMessage
import com.key.magicbook.R
import com.key.magicbook.activity.index.bookrack.BookRackFragment
import com.key.magicbook.activity.read.ReadActivity
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.db.BookDetail
import com.key.magicbook.db.BookLike
import com.key.magicbook.db.BookRank
import com.key.magicbook.db.BookReadChapter
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.CommonUtil
import com.key.magicbook.util.GlideUtils
import com.wx.goodview.GoodView
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.fragment_index_mine.toolbar
import org.greenrobot.eventbus.EventBus
import org.jsoup.nodes.Document
import org.litepal.LitePal
import org.litepal.LitePal.where

/**
 * created by key  on 2020/4/13
 */
class BookDetailActivity : MineBaseActivity<BookDetailPresenter>() {
    private var localChapterUrls: ArrayList<String>? = null
    private var bookName = ""
    private var adapter: Adapter? = null
    private var mBookDetail: BookDetail? = null
    private var goodView: GoodView? = null
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
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        more_list.layoutManager = linearLayoutManager
        adapter = Adapter()
        adapter!!.setOnItemClickListener { _,
                                           _,
                                           position ->
            val bookRank = adapter!!.data[position] as BookRank
            val busMessage = BusMessage<Document>()
            busMessage.target = BookDetailActivity::class.java.simpleName
            busMessage.specialMessage = bookRank.bookName
            busMessage.message = bookRank.bookUrl

            startActivity(Intent(this, BookDetailActivity::class.java))
            handler.postDelayed({
                EventBus.getDefault().postSticky(busMessage)
            },200)

        }
        more_list.adapter = adapter
        cache.setOnClickListener {

        }
        add.setOnClickListener {
            isBookCase()
        }
        read.setOnClickListener {
            presenter!!.loadBookReadChapters(mBookDetail!!, getUserInfo().userName)
        }
        goodView = GoodView(this)
        like.setOnClickListener {
            var imgId = R.mipmap.book_like
            if (isLike) {
                imgId = R.mipmap.book_un_like
            }
            Glide.with(this).load(imgId).into(like)
            isLike = !isLike
            goodView!!.setImage(imgId)
            goodView!!.show(like)
            if (isLike) {
                updateBookLike("true")
            } else {
                updateBookLike("false")
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
        if (busMessage.data != null) {
            runOnUiThread {
                executeData(busMessage.data as Document)
            }
        } else {
            JsoupUtils.getFreeDocument(ConstantValues.BASE_URL + bookUrl)
                .subscribe(object : CustomBaseObserver<Document>(LoadingView(this)) {
                    override fun next(o: Document?) {
                        executeData(o!!)
                    }
                })
        }
    }

    private fun executeData(document: Document) {
        mBookDetail = presenter!!.parseBookDetail(document, bookUrl)
        presenter!!.getChapters(mBookDetail!!, getUserInfo().userName)
    }

    fun loadView(bookDetail: BookDetail) {
        toolbar.title = bookDetail.bookName
        localChapterUrls = bookDetail.chapterUrls as ArrayList<String>?
        var bookAuthor = bookDetail.bookAuthor
        if (bookAuthor.contains("：")) {
            bookAuthor = bookAuthor.split("：")[1]
        }
        author.text = bookAuthor
        var lastUpdateTime = bookDetail.lastUpdateTime
        if (lastUpdateTime.contains("：")) {
            lastUpdateTime = lastUpdateTime.split("：")[1]
        }
        last_update_info_time.text = lastUpdateTime
        last_update_info_chapter.text = bookDetail.lastChapter
        intro.text = bookDetail.bookIntro
        GlideUtils.load(this, bookDetail.bookCover, book_cover)
        Thread {
            GlideUtils.loadBlur(this, bookDetail.bookCover, book_root)
        }.start()
        adapter!!.setNewData(getRankList())
        checkBookLike(bookDetail)
        checkBookCase()
        updateIsLooked()
    }


    private fun checkBookLike(bookDetail: BookDetail) {
        val find = where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?",
            bookDetail.bookName, ConstantValues.BASE_URL, bookDetail.bookUrl, getUserInfo().userName
        ).find(BookLike::class.java)
        Log.e("pile", "check like :" + find.size.toString())
        if (find.size > 0) {
            isLike = find[0].isLike == "true"
        }
        var imgId = R.mipmap.book_un_like
        if (isLike) {
            imgId = R.mipmap.book_like
        }
        if (find.size == 0) {
            val bookLike = BookLike()
            bookLike.bookName = bookDetail.bookName
            bookLike.bookAuthor = bookDetail.bookAuthor
            bookLike.bookUrl = bookDetail.bookUrl
            bookLike.bookOnlyTag = bookDetail.bookName + bookDetail.bookAuthor + bookDetail.bookUrl
            bookLike.userName = getUserInfo().userName
            bookLike.isLike = "false"
            bookLike.isBookCase = "false"
            bookLike.isLooked = "true"
            bookLike.bookCover = bookDetail.bookCover
            bookLike.baseUrl = ConstantValues.BASE_URL
            bookLike.save()
        }
        Glide.with(this).load(imgId).into(like)
    }

    override fun onResume() {
        super.onResume()
        toolbar.title = bookName
    }

   inner class Adapter() : BaseQuickAdapter<BookRank, BaseViewHolder>(R.layout.item_fragment_city_list) {
        override fun convert(helper: BaseViewHolder, item: BookRank) {
            helper.setText(R.id.name,item.bookName)
            GlideUtils.loadGif(context, helper.getView(R.id.image))
            if(item.bookCover.isEmpty()){
                val exitBookDetail1 = presenter!!.getExitBookDetail(
                    item.bookName,
                    ConstantValues.BASE_URL,
                    item.bookUrl
                )
                if(exitBookDetail1.isEmpty()){
                    JsoupUtils.getFreeDocument(ConstantValues.BASE_URL+item.bookUrl ).subscribe(object :CustomBaseObserver<Document>(){
                        override fun next(o: Document?) {
                            val parseDocument = presenter!!.parseDocument(o!!)
                            GlideUtils.load(
                                context,
                                parseDocument.bookCover,
                                helper.getView(R.id.image)
                            )
                            item.bookCover = parseDocument.bookCover
                            item.bookIntro = parseDocument.bookIntro
                            parseDocument.bookUrl = item.bookUrl
                            parseDocument.isLooked = "false"
                            parseDocument.isBookCase = "false"
                            parseDocument.save()
                        }

                    })
                }else{
                    GlideUtils.load(
                        context,
                        exitBookDetail1[0].bookCover,
                        helper.getView(R.id.image)
                    )
                }

            }else{
                GlideUtils.load(
                    context,
                    item.bookCover ,
                    helper.getView(R.id.image)
                )
            }
        }
    }


    fun bookContent(content: String, chapterPosition: Int) {
        val busMessage = BusMessage<BookDetail>()
        busMessage.data = mBookDetail
        busMessage.message = content
        busMessage.target = ReadActivity::class.java.simpleName
        busMessage.specialMessage = chapterPosition.toString()
        sendBusMessage(busMessage = busMessage)
        startActivity(Intent(this@BookDetailActivity, ReadActivity::class.java))
    }

     fun checkRead() {
        val find = where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?",
            mBookDetail!!.bookName,
            ConstantValues.BASE_URL,
            mBookDetail!!.bookUrl,
            getUserInfo().userName
        ).find(BookReadChapter::class.java)

        if (find.size == 0) {
            if (localChapterUrls!!.size - 1 >= 0) {
                val bookUrl = ConstantValues.BASE_URL + localChapterUrls!![0]
                presenter!!.getBookContent(bookUrl, 0)
            }
        } else {
            val bookReadChapter = find[find.size - 1]
            val busMessage = BusMessage<BookDetail>()
            busMessage.data = mBookDetail
            busMessage.message = bookReadChapter.bookChapterContent
            busMessage.target = ReadActivity::class.java.simpleName
            busMessage.specialMessage = (bookReadChapter.chapterNum).toString()
            sendBusMessage(busMessage = busMessage)
            startActivity(Intent(this@BookDetailActivity, ReadActivity::class.java))
        }
    }

    private fun isBookCase() {
        val find = where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ? ",
            mBookDetail!!.bookName,
            mBookDetail!!.baseUrl,
            mBookDetail!!.bookUrl,
            getUserInfo().userName
        ).find(BookLike::class.java)
        if (find.size > 0) {
            val b = find[0].isBookCase == "true"
            var isBookCase = if (b) {
                "false"
            } else {
                "true"
            }
            val bookCase = ContentValues()
            bookCase.put("isBookCase", isBookCase)
            LitePal.updateAll(
                BookLike::class.java,
                bookCase,
                "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?",
                mBookDetail!!.bookName,
                mBookDetail!!.baseUrl,
                mBookDetail!!.bookUrl,
                getUserInfo().userName
            )
            checkBookCase()

            val busMessage = BusMessage<String>()
            busMessage.target = BookRackFragment::class.java.simpleName
            busMessage.message = "bookRefresh"
            handler.postDelayed({
                EventBus.getDefault().postSticky(busMessage)
            },200)

        } else {
            Toast.makeText(this, "您还没有在数据库中添加此书籍", Toast.LENGTH_SHORT).show()
        }



    }

    private fun checkBookCase() {
        val find = where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?",
            mBookDetail!!.bookName,
            ConstantValues.BASE_URL,
            mBookDetail!!.bookUrl,
            getUserInfo().userName
        ).find(BookLike::class.java)
        if (find.size > 0) {
            val b = find[0].isBookCase == "true"
            if (b) {
                add_des.text = "从书架移除"
            } else {
                add_des.text = "加入书架"
            }

        } else {
            Toast.makeText(this, "您还没有在数据库中添加此书籍", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateIsLooked() {
        val looked = ContentValues()
        looked.put("isLooked", "true")
        LitePal.updateAll(
            BookLike::class.java,
            looked,
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?",
            mBookDetail!!.bookName,
            ConstantValues.BASE_URL,
            mBookDetail!!.bookUrl,
            getUserInfo().userName
        )
    }

    private fun updateBookLike(isLike: String) {
        val looked = ContentValues()
        looked.put("isLike", isLike)
        LitePal.updateAll(
            BookLike::class.java,
            looked,
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?",
            mBookDetail!!.bookName,
            ConstantValues.BASE_URL,
            mBookDetail!!.bookUrl,
            getUserInfo().userName
        )
    }

    private fun getRankList(): ArrayList<BookRank>? {
        val arrayList = ArrayList<BookRank>()
        val findAll = LitePal.findAll(BookRank::class.java)
        return if (findAll.size > 0) {
            var size = 10
            if (findAll.size < 10) {
                size = findAll.size - 1
            }
            val randomSet = CommonUtil.getRandomSet(findAll.size - 1, size)

            for(value in randomSet){
                arrayList.add(findAll[value])
            }
            arrayList
        } else {
            null
        }

    }

}