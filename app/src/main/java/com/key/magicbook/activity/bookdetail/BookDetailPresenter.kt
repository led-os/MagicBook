package com.key.magicbook.activity.bookdetail

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.widget.Toast
import com.allen.library.interceptor.Transformer
import com.key.keylibrary.base.BasePresenter
import com.key.keylibrary.bean.BusMessage
import com.key.magicbook.activity.index.booktype.BookTypeModel
import com.key.magicbook.activity.read.ReadActivity
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.db.BookDetail
import com.key.magicbook.db.BookReadChapter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.litepal.LitePal

/**
 * created by key  on 2020/2/27
 */
class BookDetailPresenter : BasePresenter<Activity>(),BookDetailContract.OnPresenter {
    private var model:BookDetailModel ?= null
    private var mBookTypeModel :BookTypeModel ?= null
    init {
        model = BookDetailModel()
        mBookTypeModel = BookTypeModel()
    }
    override fun getView(): BookDetailActivity{
        return iView!!.get() as BookDetailActivity
    }

    override fun parseBookDetail(document: Document, url: String): BookDetail {
        return model!!.parseBookDetail(document, url)
    }

    override fun getChapters(bookDetail: BookDetail,userName :String) {
        val localChapterUrls:ArrayList<String> = ArrayList()
        val localChapterNames :ArrayList<String> = ArrayList()
        val find = LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?  ",
            bookDetail.bookName,
            ConstantValues.BASE_URL,
            bookDetail.bookUrl,
            userName
        ).find(BookReadChapter::class.java)
        val needSaveChapters = ArrayList<BookReadChapter>()
        model!!.getChapters(bookDetail)
            .subscribe(object : Observer<String> {
            override fun onComplete() {
                bookDetail.chapterUrls = localChapterUrls
                bookDetail.chapterNames = localChapterNames
                getView().loadView(bookDetail)
                LitePal.saveAll(needSaveChapters)
            }


                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: String) {
                    val split = t!!.split("value")
                    localChapterNames!!.add(split[0])
                    localChapterUrls!!.add(split[1])
                    var isCheck = true
                    for(value in find){
                        if( value.chapterUrl == split[1]){
                            isCheck = false
                            break
                        }
                    }
                    if(isCheck){
                        val bookReadChapter = BookReadChapter()
                        bookReadChapter.bookChapterOnlyTag = bookDetail.bookName + bookDetail.bookAuthor + bookDetail.bookUrl
                        bookReadChapter.chapterName = split[0]
                        bookReadChapter.chapterUrl = split[1]
                        bookReadChapter.bookChapterContent = ""
                        bookReadChapter.begin = 0
                        bookReadChapter.chapterNum  = localChapterUrls.size-1
                        bookReadChapter.userName = userName
                        bookReadChapter.isLook = "false"
                        bookReadChapter.isCache = "false"
                        bookReadChapter.bookName = bookDetail.bookName
                        bookReadChapter.baseUrl = ConstantValues.BASE_URL
                        bookReadChapter.bookUrl = bookDetail.bookUrl
                        find.add(bookReadChapter)
                        needSaveChapters.add(bookReadChapter)
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(getView(), "加载目录失败", Toast.LENGTH_SHORT).show()
                }

            })
    }

    override fun getBookContent(bookUrl: String, chapterPosition: Int) {
        model!!.getBookContent(bookUrl, chapterPosition)
            .compose(Transformer.switchSchedulers())
            .subscribe(object : CustomBaseObserver<Element>(LoadingView(getView())){
                override fun next(o: Element?) {
                    getView().bookContent(o!!.text(),chapterPosition)
                }
            })
    }

    override fun loadBookReadChapters(bookDetail: BookDetail,userName :String) {
        val find1 = LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?",
            bookDetail.bookName ,ConstantValues.BASE_URL , bookDetail.bookUrl, userName
        ).find(BookReadChapter::class.java)

        if(find1.size > 0){
            if(find1[find1.size - 1].bookChapterContent.isEmpty()){
                val loadBookReadChapters =
                    model!!.loadBookReadChapters(bookDetail, userName)
                if(loadBookReadChapters != null){
                    model!!.loadBookReadChapters(bookDetail,userName)!!
                        .subscribe(object :CustomBaseObserver<Element>(LoadingView(getView())){
                            override fun next(o: Element?) {
                                val contentValues = ContentValues()
                                contentValues.put("bookChapterContent","\n\u3000第1"
                                        +"章\n\u3000" + o!!.text())
                                LitePal.updateAll(BookReadChapter::class.java,
                                    contentValues,
                                    "bookName = ? and baseUrl = ? and bookUrl = ? and chapterUrl = ? and userName = ?",
                                    bookDetail.bookName ,ConstantValues.BASE_URL , bookDetail.bookUrl,
                                    find1[find1.size - 1].chapterUrl,userName)
                                getView().checkRead()
                            }
                        })
                }else{
                    getView().checkRead()
                }
            }else{
                getView().checkRead()
            }
        }else{
            getView().checkRead()
        }

    }

    override fun getExitBookDetail(  bookName: String,
                                     baseUrl: String,
                                     bookUrl: String): ArrayList<BookDetail> {
       return mBookTypeModel!!.getExitBookDetail(bookName, baseUrl, bookUrl) as ArrayList<BookDetail>
    }

    override fun parseDocument(documented: Document): BookDetail {
        return mBookTypeModel!!.parseDocument(documented)
    }

    private fun filterSpecialSymbol(cacheName :String) :String{
        return  cacheName.replace("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
    }


}