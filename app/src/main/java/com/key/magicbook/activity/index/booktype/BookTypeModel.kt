package com.key.magicbook.activity.index.booktype


import com.allen.library.interceptor.Transformer
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.db.BookDetail
import com.key.magicbook.document.ParseDocumentCreator
import com.key.magicbook.jsoup.JsoupUtils
import io.reactivex.Observable
import org.jsoup.nodes.Document
import org.litepal.LitePal

/**
 * created by key  on 2020/5/10
 */
class BookTypeModel  : BookTypeContract.OnModel {
    override fun getTypeDocument(url: String) :Observable<Document>{
       return JsoupUtils.getFreeDocument(url)
            .compose(Transformer.switchSchedulers())
    }

    override fun parseHeaders(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseBookHeaders(document)
    }

    override fun parseBookDetails(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseBookDetails(document)
    }

    override fun parseDocument(documented: Document): BookDetail {
        val parseDocument = ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL)
        return parseDocument.parseBookDetail(documented)
    }

    override fun getExitBookDetail(
        bookName: String,
        baseUrl: String,
        bookUrl: String
    ): List<BookDetail> {
        return LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ?",
            bookName, baseUrl, bookUrl
        )
            .find(BookDetail::class.java)
    }

}