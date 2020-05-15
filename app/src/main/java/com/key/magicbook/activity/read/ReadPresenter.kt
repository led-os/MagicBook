package com.key.magicbook.activity.read

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.allen.library.interceptor.Transformer
import com.key.keylibrary.base.BasePresenter
import com.key.magicbook.activity.bookdetail.BookDetailModel
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.db.BookDetail
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.Exception

/**
 * created by key  on 2020/2/27
 */
class ReadPresenter : BasePresenter<Activity>(),ReadContract.OnPresenter {
    private var detailModel:BookDetailModel ?= null
    init {
        detailModel = BookDetailModel()
    }
    override fun getView(): ReadActivity {
        return iView!!.get() as ReadActivity
    }

    override fun getChapter(bookDetail: BookDetail) {
        val localChapterUrls:ArrayList<String> = ArrayList()
        val localChapterNames :ArrayList<String> = ArrayList()
        detailModel!!.getChapters(bookDetail)
            .subscribe(object : CustomBaseObserver<String>(){

                override fun onComplete() {
                    super.onComplete()
                    bookDetail.chapterUrls = localChapterUrls
                    bookDetail.chapterNames = localChapterNames
                    try {
                        getView().loadChapter(bookDetail)
                    }catch (e:Exception){
                        Log.e("error","view null")
                    }

                }


                override fun onError(e: Throwable) {
                    super.onError(e)
                    Toast.makeText(getView(), "加载目录失败", Toast.LENGTH_SHORT).show()
                }

                override fun next(o: String?) {
                    val split = o!!.split("value")
                    localChapterNames.add(split[0])
                    localChapterUrls.add(split[1])
                }

            })

    }

    override fun parseBookDetail(document: Document, url: String): BookDetail {
        return  detailModel!!.parseBookDetail(document, url)
    }

    override fun getBookContent(bookUrl: String, chapterPosition: Int) {
        detailModel!!.getBookContent(bookUrl, chapterPosition)
            .compose(Transformer.switchSchedulers())
            .subscribe(object : CustomBaseObserver<Element>(LoadingView(getView())){
                override fun next(o: Element?) {
                    getView().bookContent(o!!.text(),chapterPosition)
                }
            })
    }
}