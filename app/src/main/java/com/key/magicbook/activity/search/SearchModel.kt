package com.key.magicbook.activity.search

import android.util.Log
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.bean.BookSearchHistory
import com.key.magicbook.bean.BookSearchResult
import com.key.magicbook.db.BookDetail
import com.key.magicbook.document.ParseDocumentCreator
import com.key.magicbook.jsoup.JsoupUtils
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import org.jsoup.nodes.Document
import org.litepal.LitePal

/**
 * created by key  on 2020/1/5
 */
class SearchModel :SearchContract.OnModel {
    override fun search(keyword: String): Observable<ArrayList<BookSearchResult>>{

        return JsoupUtils.getDingDianSearch(keyword)
            .flatMap(Function<Document, ObservableSource<ArrayList<BookSearchResult>>> {
                val select = it!!.select(" span:nth-child(2) > a:nth-child(1)")
                val select1 = it!!.select(" span:nth-child(4)")
                var zipWith: ArrayList<BookSearchResult> = ArrayList()


                for ((index, value) in select.withIndex()) {
                    val bookSearchResult = BookSearchResult()
                    bookSearchResult.name = value.text()
                    bookSearchResult.author = select1[index + 1].text()
                    bookSearchResult.bookUrl = value.attr("href")
                    zipWith!!.add(bookSearchResult)
                }
                Observable.create { observableEmitter ->
                    observableEmitter.onNext(zipWith)
                    observableEmitter.onComplete()
                }
            })
    }

    override fun getLocalSearch() :List<BookSearchHistory>{
        return  LitePal.findAll(BookSearchHistory::class.java)

    }

    override fun parseDocument(document: Document): BookDetail {
        val parseDocument = ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL)
        return parseDocument.parseBookDetail(document)
    }

    override fun getExitBookDetail(bookName: String, baseUrl:String, bookUrl :String): List<BookDetail> {
        return LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ?",
            bookName, baseUrl, bookUrl
        )
            .find(BookDetail::class.java)

    }
}