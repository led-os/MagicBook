package com.key.magicbook.activity.bookdetail

import com.key.magicbook.base.BaseContract
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.bean.BookSearchHistory
import com.key.magicbook.bean.BookSearchResult
import io.reactivex.Observable
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/1/5
 */
class BookDetailContract : BaseContract() {
  interface OnModel{
    fun parseBookDetail(document: Document,url :String):BookDetail
    fun getChapters(bookDetail: BookDetail):Observable<String>
  }

  interface OnPresenter{
    fun parseBookDetail(document: Document,url :String):BookDetail
    fun getChapters(bookDetail: BookDetail)
  }
}