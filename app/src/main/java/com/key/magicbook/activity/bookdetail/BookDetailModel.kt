package com.key.magicbook.activity.bookdetail

import com.allen.library.interceptor.Transformer
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.db.BookDetail
import com.key.magicbook.document.ParseDocumentCreator
import com.key.magicbook.jsoup.JsoupUtils
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * created by key  on 2020/1/5
 */
class BookDetailModel :BookDetailContract.OnModel {
    override fun parseBookDetail(document: Document,url :String): BookDetail {
        val parseDocument = ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL)
        val parseBookDetail = parseDocument.parseBookDetail(document)
        parseBookDetail.bookUrl = url
        return parseBookDetail
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

    override fun getBookContent(bookUrl: String, chapterPosition: Int): Observable<Element> {
       return JsoupUtils.connectFreeUrl(bookUrl, "#content")
    }


}