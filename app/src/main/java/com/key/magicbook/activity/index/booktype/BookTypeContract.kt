package com.key.magicbook.activity.index.booktype

import com.key.magicbook.base.BaseContract
import com.key.magicbook.db.BookDetail
import io.reactivex.Observable
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/1/5
 */
class BookTypeContract : BaseContract() {
  interface OnModel{
    fun getTypeDocument(url :String) :Observable<Document>
    fun parseHeaders(document: Document):List<BookDetail>
    fun parseBookDetails(document :Document):List<BookDetail>
  }

  interface OnPresenter{
    fun getTypeDocument(url :String)
    fun parseHeaders(document :Document):List<BookDetail>
    fun parseBookDetails(document :Document):List<BookDetail>
  }
}