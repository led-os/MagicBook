package com.key.magicbook.activity.search

import android.app.Activity
import com.allen.library.interceptor.Transformer
import com.key.keylibrary.base.BasePresenter
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.bean.BookSearchResult
import com.key.magicbook.db.BookDetail
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/4/1
 */
class SearchPresenter : BasePresenter<Activity>() ,SearchContract.OnPresenter{
    private  var searchModel:SearchModel ?= null;
    init {
         searchModel = SearchModel()
    }
    override fun getView(): SearchActivity {
        return iView!!.get() as SearchActivity
    }

    override fun search(keyword: String){
        val search = searchModel!!.search(keyword)
        search .compose(Transformer.switchSchedulers())
            .subscribe(object :
                CustomBaseObserver<ArrayList<BookSearchResult>>(LoadingView(getView())) {
                override fun next(o: ArrayList<BookSearchResult>) {
                    if (o.size > 0) {
                       getView().getBookResult(o)
                    } else {
                      getView().resultNull()
                    }
                }


                override fun onError(e: Throwable) {
                    super.onError(e)
                    getView().searchError()
                }
            })
    }

    override fun getLocalSearchHistory() {
        getView().loadFlow( searchModel!!.getLocalSearch())

    }

    override fun parseDocument(documented: Document):BookDetail {
       return  searchModel!!.parseDocument(documented)
    }
}