package com.key.magicbook.activity.index.booktype

import androidx.fragment.app.Fragment
import com.key.keylibrary.base.BasePresenter
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.db.BookDetail
import com.key.magicbook.document.ParseDocumentCreator
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/5/10
 */
class BookTypePresenter  : BasePresenter<Fragment>(),BookTypeContract.OnPresenter {
    private var model :BookTypeModel ? = null
    init {
        model = BookTypeModel()
    }
    override fun getView(): BookTypeFragment {
        return iView!!.get() as BookTypeFragment
    }

    override fun getTypeDocument(url: String) {
        model!!.getTypeDocument(url) .subscribe(object : CustomBaseObserver<Document>() {
            override fun next(o: Document?) {
                getView().loadDocument(o!!)
            }
        })
    }

    override fun parseHeaders(document: Document): ArrayList<BookDetail> {
      return model!!.parseHeaders(document) as ArrayList<BookDetail>
    }

    override fun parseBookDetails(document: Document): ArrayList<BookDetail> {
        return model!!.parseBookDetails(document) as ArrayList<BookDetail>
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
        return model!!.getExitBookDetail(bookName, baseUrl, bookUrl)
    }
}