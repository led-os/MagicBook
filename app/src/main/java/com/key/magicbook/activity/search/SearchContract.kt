package com.key.magicbook.activity.search

import com.key.magicbook.base.BaseContract
import com.key.magicbook.bean.BookSearchHistory
import com.key.magicbook.bean.BookSearchResult
import io.reactivex.Observable

/**
 * created by key  on 2020/1/5
 */
class SearchContract : BaseContract() {
  interface OnModel{
    fun search(keyword: String): Observable<ArrayList<BookSearchResult>>
    fun getLocalSearch() :List<BookSearchHistory>
  }

  interface OnPresenter{
    fun search(keyword :String)
    fun getLocalSearchHistory()
  }
}