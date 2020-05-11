package com.key.magicbook.activity.index.bookcity


import com.allen.library.interceptor.Transformer
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.document.ParseDocumentCreator
import com.key.magicbook.jsoup.JsoupUtils.getFreeDocument
import io.reactivex.Observable
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/5/10
 */
class BookCityModel:BookCityContract.OnModel {
    override fun loadDocument(url: String): Observable<Document> {
        return getFreeDocument(ConstantValues.BASE_URL)
            .compose(Transformer.switchSchedulers())
    }


    override fun loadPile(document: Document): List<BookDetail> {
       return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parsePile(document)
    }

    override fun loadTypeOne(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseTypeOne(document)
    }

    override fun loadTypeTwo(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseTypeTwo(document)
    }

    override fun loadTypeThree(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseTypeThree(document)
    }

    override fun loadTypeFour(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseTypeFour(document)
    }

    override fun loadTypeFive(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseTypeFive(document)
    }

    override fun loadTypeSix(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseTypeSix(document)
    }

    override fun loadTypeSeven(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseTypeSeven(document)
    }

    override fun loadTypeEight(document: Document): List<BookDetail> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).parseTypeEight(document)
    }
}