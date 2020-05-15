package com.key.magicbook.activity.index.bookcity

import androidx.fragment.app.Fragment
import com.key.keylibrary.base.BasePresenter
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.db.BookDetail
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/5/10
 */
class BookCityPresenter  : BasePresenter<Fragment>() ,BookCityContract.OnPresenter{
    private var model :BookCityModel ?= null
    init {
        model = BookCityModel()
    }
    override fun getView(): BookCityFragment {
        return iView!!.get() as BookCityFragment
    }

    override fun loadDocument(url: String) {
         model!!.loadDocument(url)  .subscribe(object : CustomBaseObserver<Document>() {
             override fun next(o: Document?) {
                    getView().setDocument(o!!)
             }
         })
    }

    override fun loadPile(document: Document) {
        getView().loadPile(model!!.loadPile(document))
    }

    override fun loadTypeOne(document: Document): List<BookDetail> {
       return model!!.loadTypeOne(document)
    }

    override fun loadTypeTwo(document: Document) : List<BookDetail>{
        return model!!.loadTypeTwo(document)
    }

    override fun loadTypeThree(document: Document) : List<BookDetail>{
        return model!!.loadTypeThree(document)
    }

    override fun loadTypeFour(document: Document) : List<BookDetail>{
        return model!!.loadTypeFour(document)
    }

    override fun loadTypeFive(document: Document): List<BookDetail> {
        return model!!.loadTypeFive(document)
    }

    override fun loadTypeSix(document: Document): List<BookDetail> {
        return model!!.loadTypeSix(document)
    }

    override fun loadTypeSeven(document: Document): List<BookDetail> {
        return model!!.loadTypeSeven(document)
    }

    override fun loadTypeEight(document: Document): List<BookDetail> {
        return model!!.loadTypeEight(document)
    }

    override fun parseDocument(documented: Document): BookDetail {
        return model!!.parseDocument(documented)
    }

    override fun getExitBookDetail(
        bookName: String,
        baseUrl: String,
        bookUrl: String
    ): List<BookDetail> {
        return model!!.getExitBookDetail(bookName, baseUrl, bookUrl)    }


}