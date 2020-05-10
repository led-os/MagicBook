package com.key.magicbook.activity.index.bookcity

import com.key.magicbook.base.BaseContract
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.bean.BookSearchHistory
import com.key.magicbook.bean.BookSearchResult
import io.reactivex.Observable
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/1/5
 */
class BookCityContract : BaseContract() {
  interface OnModel{
    fun loadDocument(url :String) :Observable<Document>
    fun loadPile(document:Document): List<BookDetail>
    fun loadTypeOne(document :Document): List<BookDetail>
    fun loadTypeTwo(document :Document): List<BookDetail>
    fun loadTypeThree(document :Document): List<BookDetail>
    fun loadTypeFour(document :Document): List<BookDetail>
    fun loadTypeFive(document :Document): List<BookDetail>
    fun loadTypeSix(document :Document): List<BookDetail>
    fun loadTypeSeven(document :Document): List<BookDetail>
    fun loadTypeEight(document :Document): List<BookDetail>
  }

  interface OnPresenter{
    fun loadDocument(url :String)
    fun loadPile(document:Document)
    fun loadTypeOne(document :Document): List<BookDetail>
    fun loadTypeTwo(document :Document): List<BookDetail>
    fun loadTypeThree(document :Document): List<BookDetail>
    fun loadTypeFour(document :Document): List<BookDetail>
    fun loadTypeFive(document :Document): List<BookDetail>
    fun loadTypeSix(document :Document): List<BookDetail>
    fun loadTypeSeven(document :Document): List<BookDetail>
    fun loadTypeEight(document :Document): List<BookDetail>
  }
}