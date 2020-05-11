package com.key.magicbook.activity.index.booktype


import com.allen.library.interceptor.Transformer
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.document.ParseDocumentCreator
import com.key.magicbook.jsoup.JsoupUtils
import io.reactivex.Observable
import org.jsoup.nodes.Document

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

}