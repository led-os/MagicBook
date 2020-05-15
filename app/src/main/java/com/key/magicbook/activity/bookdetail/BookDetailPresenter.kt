package com.key.magicbook.activity.bookdetail

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.allen.library.interceptor.Transformer
import com.key.keylibrary.base.BasePresenter
import com.key.keylibrary.bean.BusMessage
import com.key.magicbook.activity.read.ReadActivity
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.db.BookDetail
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * created by key  on 2020/2/27
 */
class BookDetailPresenter : BasePresenter<Activity>(),BookDetailContract.OnPresenter {
    private var model:BookDetailModel ?= null
    init {
        model = BookDetailModel()
    }
    override fun getView(): BookDetailActivity{
        return iView!!.get() as BookDetailActivity
    }

    override fun parseBookDetail(document: Document, url: String): BookDetail {
        return model!!.parseBookDetail(document, url)
    }

    override fun getChapters(bookDetail: BookDetail) {
        val localChapterUrls:ArrayList<String> = ArrayList()
        val localChapterNames :ArrayList<String> = ArrayList()
        model!!.getChapters(bookDetail)
            .subscribe(object : Observer<String> {
            override fun onComplete() {
                bookDetail.chapterUrls = localChapterUrls
                bookDetail.chapterNames = localChapterNames
                getView().loadView(bookDetail)
            }


                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: String) {
                    val split = t!!.split("value")
                    localChapterNames!!.add(split[0])
                    localChapterUrls!!.add(split[1])
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
        model!!.loadBookReadChapters(bookDetail,userName)
    }


}