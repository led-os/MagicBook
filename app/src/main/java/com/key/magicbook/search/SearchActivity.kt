package com.key.magicbook.search

import android.util.Log
import com.allen.library.base.BaseObserver
import com.allen.library.interceptor.Transformer
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.api.ApiHelper
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.jsoup.JsoupUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_set.toolbar
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit


/**
 * created by key  on 2020/4/1
 */
class SearchActivity : MineBaseActivity<SearchPresenter>() {
    val searchBaseUrl = "https://www.dingdiann.com/"
    private  var urls = ArrayList<String>()
    private  var names = ArrayList<String>()
    override fun createPresenter(): SearchPresenter {
        return SearchPresenter()
    }

    override fun initView() {
        setTitle(toolbar)
        initToolbar(toolbar)

        scroll.layoutParams.height = UiUtils.getScreenHeight(this)/4

        search.setOnClickListener {

         JsoupUtils.getDingDianSearch(name.text.toString())
                .flatMap(Function<Document, ObservableSource<Observable<String>>> {
                    val select = it!!.select(" span:nth-child(2) > a:nth-child(1)")
                    val detail = getDetail(searchBaseUrl + select[0].attr("href"))
                    var zipWith :Observable<String> ?= null
                     zipWith =
                          detail.zipWith(getDetail(searchBaseUrl + select[1].attr("href")),
                            BiFunction<Document, Document, String> { t1, t2 ->
                                "index is 1\n"
                            })
                    for (value in 2 until select.size) {
                       zipWith = zipWith!!.zipWith(getDetail(searchBaseUrl + select[value].attr("href")),
                           BiFunction<String, Document, String> { t1, t2 ->
                               t1 + value+"\n"
                           })
                    }

                    Observable.create { observableEmitterDocument ->
                        observableEmitterDocument.onNext(zipWith)
                        observableEmitterDocument.onComplete()
                    }
                })
                 .compose(Transformer.switchSchedulers())
                 .subscribe(object :CustomBaseObserver<Observable<String>>(LoadingView(this)){
                    override fun next(o: Observable<String>) {
                        o.subscribe(object :CustomBaseObserver<String>(){
                            override fun next(o: String?) {
                                 Log.e("qidian",o)
                            }
                        })
                    }
                })
        }

    }
    override fun setLayoutId(): Int {
        return R.layout.activity_search
    }


    private fun getDetail(value :String) :Observable<Document> {
      return  ApiHelper.getFreeUrlApi().freeUrl(value)
            .compose(Transformer.switchSchedulers())
            .flatMap(Function<ResponseBody, ObservableSource<Document?>> { s: ResponseBody ->
                val parse = Jsoup.parse(s.string())
                Observable.create(ObservableOnSubscribe { e: ObservableEmitter<Document?> ->
                    e.onNext(parse)
                    e.onComplete()
                })
            })
    }
}