package com.key.magicbook.activity.index.bookrack

import androidx.fragment.app.Fragment
import com.key.keylibrary.base.BasePresenter
import com.key.magicbook.activity.index.booktype.BookTypeModel
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.db.BookDetail
import com.key.magicbook.document.ParseDocumentCreator
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/5/10
 */
class BookRackPresenter  : BasePresenter<Fragment>(),BookRackContract.OnPresenter {
    private var model :BookRackModel ? = null
    private var bookTypeModel :BookTypeModel ?= null
    init {
        model = BookRackModel()
        bookTypeModel = BookTypeModel()
    }
    override fun getView(): BookRackFragment {
        return iView!!.get() as BookRackFragment
    }

    override fun parseBookRank(url: String) {
        model!!.parseBookRank(url).subscribe(object :CustomBaseObserver<Document>(){
            override fun next(o: Document?) {
                 val parseDocument = ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL)
                 getView().parseBookRack(parseDocument.parseRankUrls(o!!) as ArrayList<HashMap<String, String>>)
            }
        })
    }

    override fun parseBookRankContent(url: String) {
       model!!.parseBookRankContent(url)
    }

    override fun getExitBookDetail(
        bookName: String,
        baseUrl: String,
        bookUrl: String
    ): List<BookDetail> {
      return  bookTypeModel!!.getExitBookDetail(bookName, baseUrl, bookUrl)
    }

    override fun parseDocument(documented: Document): BookDetail {
        return bookTypeModel!!.parseDocument(documented)
    }


}