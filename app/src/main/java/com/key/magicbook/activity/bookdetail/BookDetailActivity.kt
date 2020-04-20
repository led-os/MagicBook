package com.key.magicbook.activity.bookdetail

import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.allen.library.interceptor.Transformer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.read.ReadActivity
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.jsoup.JsoupUtils
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.fragment_index_mine.toolbar
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * created by key  on 2020/4/13
 */
class BookDetailActivity : MineBaseActivity<BookDetailPresenter>() {
    private val baseUrl = "https://www.dingdiann.com/"
    private var localChapterUrls: ArrayList<String>? = null
    private var localChapterNames: ArrayList<String>? = null
    private var bookName = ""
    private var adapter:Adapter ?= null
    private var mBookDetail :BookDetail ?=null
    override fun createPresenter(): BookDetailPresenter? {
        return BookDetailPresenter()
    }

    override fun initView() {
        setTitle(toolbar)
        book_bg.layoutParams.height = UiUtils.getScreenHeight(this) / 4
        toolbar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
        list.layoutManager = LinearLayoutManager(this)
        adapter = Adapter()
        adapter!!.setOnItemClickListener { adapter,
                                         view,
                                         position ->
            val bookUrl = baseUrl + localChapterUrls!![position]
            val list = adapter.data as List<String>
            getBookContent(bookUrl,list[position])
        }
        list.adapter = adapter
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_book_detail
    }

    override fun receiveMessage(busMessage: BusMessage<Any>) {
        super.receiveMessage(busMessage)
        bookName = busMessage.specialMessage
        runOnUiThread {
            executeData(busMessage.data as Document)
        }
    }

    private fun executeData(document: Document) {
        val img = document.select("#fmimg > img:nth-child(1)")
        val name = document.select("#info > h1:nth-child(1)")
        val author = document.select("#info > p:nth-child(2)")
        val update = document.select("#info > p:nth-child(4)")
        val lastChapter = document.select("#info > p:nth-child(5) > a")
        val intro = document.select("#intro")
        val select = document.select("#list > dl > dd")


        val bookDetail = BookDetail()
        bookDetail.bookCover = baseUrl + img.text()
        bookDetail.bookName = name.text()
        bookDetail.bookAuthor = author.text()
        bookDetail.lastUpdateTime = update.text()
        bookDetail.lastChapter = lastChapter.text()
        bookDetail.bookIntro = intro.text()
        Observable.fromArray(select.reversed())
            .compose(Transformer.switchSchedulers())
            .flatMap(Function<List<Element>, ObservableSource<String>> {
                localChapterUrls = ArrayList()
                localChapterNames = ArrayList()
                Observable.create { observableEmitter ->
                    for (value in it) {
                        observableEmitter.onNext(
                            value.text() + "value" + value.select("a").attr("href")
                        )
                    }

                    observableEmitter.onComplete()
                }

            }).distinct()
            .subscribe(object : Observer<String> {
                override fun onComplete() {
                    bookDetail.chapterNames = localChapterNames
                    bookDetail.chapterUrls = localChapterUrls
                    loadView(bookDetail)
                }

                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(value: String?) {
                    val split = value!!.split("value")
                    localChapterNames!!.add(split[0])
                    localChapterUrls!!.add(split[1])
                }

                override fun onError(e: Throwable?) {
                    Toast.makeText(this@BookDetailActivity, "加载目录失败", Toast.LENGTH_SHORT).show()
                }

            })

    }

    private fun loadView(bookDetail: BookDetail) {
        mBookDetail = bookDetail
        toolbar.title = bookDetail.bookName
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
        adapter!!.setNewData(bookDetail.chapterNames)
    }


    override fun onResume() {
        super.onResume()
        toolbar.title = bookName
    }

    public class Adapter() :BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_chapter){
        override fun convert(helper: BaseViewHolder, item: String) {
             helper.setText(R.id.chapter,item)
        }
    }


   private fun getBookContent(bookUrl :String,chapter :String) :String{
       val connectFreeUrl = JsoupUtils.connectFreeUrl(bookUrl, "#content")
       connectFreeUrl
           .compose(Transformer.switchSchedulers())
           .subscribe(object :CustomBaseObserver<Element>(LoadingView(this)){
           override fun next(o: Element?) {
               val busMessage = BusMessage<BookDetail>()
               busMessage.data = mBookDetail
               busMessage.message = o!!.text()
               busMessage.target = ReadActivity::class.java.simpleName
               busMessage.specialMessage = chapter
               sendBusMessage(busMessage = busMessage)
               startActivity(Intent(this@BookDetailActivity,ReadActivity::class.java))
           }
       })
       return ""
    }
}