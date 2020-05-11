package com.key.magicbook.activity.bookdetail

import android.app.Activity
import android.widget.Toast
import com.key.keylibrary.base.BasePresenter
import com.key.magicbook.base.BaseContract
import com.key.magicbook.bean.BookDetail
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.jsoup.nodes.Document

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

        var localChapterUrls:ArrayList<String> = ArrayList()
        var localChapterNames :ArrayList<String> = ArrayList()
        model!!.getChapters(bookDetail)
            .subscribe(object : Observer<String> {
            override fun onComplete() {
                bookDetail.chapterUrls = localChapterUrls
                bookDetail.chapterNames = localChapterNames
                getView().loadView(bookDetail)
            }

            override fun onSubscribe(d: Disposable?) {

            }

            override fun onNext(value: String?) {
                val split = value!!.split("value")
                localChapterNames!!.add(split[0])
                localChapterUrls!!.add(split[1])
            }

            override fun onError(e: Throwable?) {
                Toast.makeText(getView(), "加载目录失败", Toast.LENGTH_SHORT).show()
            }

        })
    }
}