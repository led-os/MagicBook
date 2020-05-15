package com.key.magicbook.activity.read

import com.key.magicbook.base.BaseContract
import com.key.magicbook.bean.BookSearchHistory
import com.key.magicbook.bean.BookSearchResult
import com.key.magicbook.db.BookDetail
import io.reactivex.Observable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * created by key  on 2020/1/5
 */
class ReadContract : BaseContract() {
  interface OnModel{


  }

  interface OnPresenter{

    fun getChapter(bookDetail : BookDetail)
    fun parseBookDetail(document: Document, url :String): BookDetail
    fun getBookContent(bookUrl :String,chapterPosition :Int)
  }
}