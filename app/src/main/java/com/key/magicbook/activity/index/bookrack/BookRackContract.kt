package com.key.magicbook.activity.index.bookrack

import com.key.magicbook.base.BaseContract
import com.key.magicbook.db.BookDetail
import io.reactivex.Observable
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/1/5
 */
class BookRackContract : BaseContract() {
  interface OnModel{

    fun parseBookRank(url :String)  :Observable<Document>
    fun parseBookRankContent(url :String)
  }

  interface OnPresenter{

    fun parseBookRank(url :String)
    fun parseBookRankContent(url :String)
    fun getExitBookDetail(bookName: String, baseUrl:String, bookUrl :String):List<BookDetail>
    fun parseDocument(documented: Document) :BookDetail
  }
}