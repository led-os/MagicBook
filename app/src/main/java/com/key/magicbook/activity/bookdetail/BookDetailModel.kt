package com.key.magicbook.activity.bookdetail

import com.allen.library.interceptor.Transformer
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.bean.BookDetail
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * created by key  on 2020/1/5
 */
class BookDetailModel :BookDetailContract.OnModel {
    override fun parseBookDetail(document: Document,url :String):BookDetail{

        val img = document.select("#fmimg > img")
        val name = document.select("#info > h1:nth-child(1)")
        val author = document.select("#info > p:nth-child(2)")
        val update = document.select("#info > p:nth-child(4)")
        val lastChapter = document.select("#info > p:nth-child(5) > a")
        val intro = document.select("#intro")
        val select = document.select("#list > dl > dd")

        val bookDetail = BookDetail()
        bookDetail.bookCover = ConstantValues.BASE_URL + img.attr("src")
        bookDetail.bookName = name.text()
        bookDetail.bookAuthor = author.text()
        bookDetail.lastUpdateTime = update.text()
        bookDetail.lastChapter = lastChapter.text()
        bookDetail.bookIntro = intro.text()
        bookDetail.bookUrl = url
        bookDetail.chapterElements = select

        return bookDetail
    }

    override fun getChapters(bookDetail: BookDetail) :Observable<String>{

       return Observable.fromArray(bookDetail.chapterElements.reversed())
            .compose(Transformer.switchSchedulers())
            .flatMap(Function<List<Element>, ObservableSource<String>> {
                Observable.create { observableEmitter ->
                    for (value in it) {
                        observableEmitter.onNext(
                            value.text() + "value" + value.select("a").attr("href")
                        )
                    }
                    observableEmitter.onComplete()
                }
            }).distinct()
    }


}