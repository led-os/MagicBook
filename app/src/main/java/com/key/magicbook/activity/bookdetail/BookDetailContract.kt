package com.key.magicbook.activity.bookdetail

import com.key.magicbook.base.BaseContract
import com.key.magicbook.db.BookDetail
import io.reactivex.Observable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * created by key  on 2020/1/5
 */
class BookDetailContract : BaseContract() {
  interface OnModel{
    fun parseBookDetail(document: Document,url :String): BookDetail
    fun getChapters(bookDetail: BookDetail):Observable<String>
    fun getBookContent(bookUrl :String,chapterPosition :Int):Observable<Element>
    fun loadBookReadChapters(bookDetail: BookDetail,userName :String):Observable<Element>?
  }

  interface OnPresenter{
    fun parseBookDetail(document: Document,url :String): BookDetail
    fun getChapters(bookDetail: BookDetail,userName :String)
    fun getBookContent(bookUrl :String,chapterPosition :Int)
    fun loadBookReadChapters(bookDetail: BookDetail,userName :String)
    fun getExitBookDetail(  bookName: String,
                            baseUrl: String,
                            bookUrl: String):ArrayList<BookDetail>
    fun parseDocument(documented: Document) :BookDetail
  }
}