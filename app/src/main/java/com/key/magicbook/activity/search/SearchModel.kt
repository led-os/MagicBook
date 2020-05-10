package com.key.magicbook.activity.search

import android.util.Log
import com.key.magicbook.bean.BookSearchHistory
import com.key.magicbook.bean.BookSearchResult
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
                    Log.e("pile", value.attr("href"))
                    Log.e("pile", value.text())
                    Log.e("pile", select1[index + 1].text())
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
}