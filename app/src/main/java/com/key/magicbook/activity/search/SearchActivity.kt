package com.key.magicbook.activity.search

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allen.library.interceptor.Transformer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.api.ApiHelper
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.base.RemindDialogClickListener
import com.key.magicbook.bean.BookSearchHistory
import com.key.magicbook.bean.BookSearchResult
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.DialogUtil
import com.key.magicbook.util.GlideUtils
import com.transitionseverywhere.Fade
import com.transitionseverywhere.TransitionManager
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_set.toolbar
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.litepal.LitePal


/**
 * created by key  on 2020/4/1
 */
class SearchActivity : MineBaseActivity<SearchPresenter>() {
    private val searchBaseUrl = "https://www.dingdiann.com/"
    private var localDocuments: ArrayList<Document> = ArrayList()
    private var adapter: Adapter? = null
    private var mInputMethodManager:InputMethodManager ?= null
    override fun createPresenter(): SearchPresenter {
        return SearchPresenter()
    }

    override fun initView() {
        mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        setTitle(toolbar)
        initToolbar(toolbar)
        getLocalSearchHistory()
        up.setOnClickListener {
            val fade = Fade()
            fade.duration = 1200
            TransitionManager.beginDelayedTransition(history,fade)
            history.visibility = View.GONE
            clearFocus()
        }

        scroll.layoutParams.height = UiUtils.getScreenHeight(this) / 4
        search.setOnClickListener {
            val searchName = name.text.toString()
            checkLocalHistory(searchName)
            search(searchName)
            clearFocus()
        }
        val linearLayoutManager = LinearLayoutManager(this)
        list.layoutManager = linearLayoutManager as RecyclerView.LayoutManager?
        adapter = Adapter(R.layout.item_book_search)
        list.adapter = adapter
        list.layoutParams.height = UiUtils.getScreenHeight(this) - UiUtils.measureView(toolbar)[1]
        scroll.layoutParams.height = UiUtils.getScreenHeight(this) / 3
        delete_history.setOnClickListener {
            var remindDialog: AlertDialog? = null
            val remindDialogClickListener = RemindDialogClickListener {
                if (it) {
                    val deleteAll = LitePal.deleteAll(BookSearchHistory::class.java)
                    if (deleteAll > 0) {
                        remindDialog!!.dismiss()
                        getLocalSearchHistory()
                        Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show()
                        history.visibility = View.GONE
                    }
                }
            }
            remindDialog = DialogUtil.getRemindDialog(
                this,
                "是否删除搜索记录?",
                remindDialogClickListener
            )
        }


        name.setOnClickListener {
            name.isFocusable = true
            name.isFocusableInTouchMode = true
            name.requestFocus()
            name.findFocus()
            if(mInputMethodManager!!.isActive){
                mInputMethodManager!!.showSoftInput(name, InputMethodManager.SHOW_FORCED)
            }
            if(history.visibility != View.VISIBLE ){
                val fade = Fade()
                fade.duration = 1000
                TransitionManager.beginDelayedTransition(history,fade)
                history.visibility = View.VISIBLE
            }
        }


    }


    private fun checkLocalHistory(name: String) {
        val searchHistory = LitePal.findAll(BookSearchHistory::class.java)
        var check = true
        for (value in searchHistory) {
            if (value.name == name) {
                check = false
            }
        }
        if (check) {
            val bookSearchHistory = BookSearchHistory()
            bookSearchHistory.name = name
            bookSearchHistory.save()
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_search
    }


    private fun search(name: String) {
        hintKeyBoard()
        getLocalSearchHistory()
        JsoupUtils.getDingDianSearch(name)
            .flatMap(Function<Document, ObservableSource<Observable<ArrayList<Document>>>> {
                val select = it!!.select(" span:nth-child(2) > a:nth-child(1)")
                var zipWith: Observable<ArrayList<Document>>? = null
                localDocuments = ArrayList<Document>()
                if (select.size >= 2) {
                    val detail = getDetail(searchBaseUrl + select[0].attr("href"))
                    zipWith =
                        detail.zipWith(getDetail(searchBaseUrl + select[1].attr("href")),
                            BiFunction<Document, Document, ArrayList<Document>> { t1, t2 ->
                                localDocuments.add(t1)
                                localDocuments.add(t2)
                                localDocuments
                            })

                    for (value in 2 until select.size) {
                        zipWith =
                            zipWith!!.zipWith(getDetail(searchBaseUrl + select[value].attr("href")),
                                BiFunction<ArrayList<Document>, Document, ArrayList<Document>> { t1, t2 ->
                                    t1.add(t2)
                                    t1
                                })
                    }
                } else if (select.size == 1) {
                    val detail = getDetail(searchBaseUrl + select[0].attr("href"))
                    detail.flatMap(Function<Document, ObservableSource<ArrayList<Document>>> { document ->
                        localDocuments.add(document)
                        Observable.create { emitter ->
                            emitter.onNext(localDocuments)
                            emitter.onComplete()
                        }
                    })
                }

                Observable.create { observableEmitterDocument ->
                    observableEmitterDocument.onNext(zipWith)
                    observableEmitterDocument.onComplete()
                }

            })
            .compose(Transformer.switchSchedulers())
            .subscribe(object :
                CustomBaseObserver<Observable<ArrayList<Document>>>(LoadingView(this@SearchActivity)) {
                override fun next(o: Observable<ArrayList<Document>>) {
                    o.compose(Transformer.switchSchedulers())
                        .subscribe(object :
                            CustomBaseObserver<ArrayList<Document>>(LoadingView(this@SearchActivity)) {
                            override fun next(o: ArrayList<Document>?) {
                                loadView(o!!)
                            }

                            override fun onError(e: Throwable) {
                                super.onError(e)
                                loadView(localDocuments)
                            }
                        })
                }


                override fun onError(e: Throwable) {
                    super.onError(e)
                    Toast.makeText(this@SearchActivity, "暂无您搜索的书籍或作者", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun getLocalSearchHistory() {
        val searchHistory = LitePal.findAll(BookSearchHistory::class.java)
        flow.removeAllViews()
        if (searchHistory.size > 0) {
            for (value in searchHistory) {
                val textView = TextView(this)
                textView.setBackgroundResource(R.drawable.shape_bg)
                textView.gravity = Gravity.CENTER
                val marginLayoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val margin = UiUtils.dip2px(6f)
                marginLayoutParams.rightMargin = margin
                marginLayoutParams.leftMargin = margin
                marginLayoutParams.topMargin = margin / 2
                marginLayoutParams.bottomMargin = margin / 2
                textView.layoutParams = marginLayoutParams
                val padding = UiUtils.dip2px(16f)
                textView.setPadding(padding, padding / 2, padding, padding / 2)
                textView.text = value.name
                textView.setOnClickListener {
                    val toString = textView.text.toString()
                    if (toString.isNotEmpty()) {
                        name.setText(toString)
                        search(toString)
                        clearFocus()
                    } else {
                        Toast.makeText(this, "请输入书名或作者名称", Toast.LENGTH_SHORT).show()
                    }
                }
                flow.addView(textView)
            }
        }
    }

    private fun getDetail(value: String): Observable<Document> {
        return ApiHelper.getFreeSecondUrlApi().freeSecondUrl(value)
            .compose(Transformer.switchSchedulers())
            .flatMap(Function<ResponseBody, ObservableSource<Document?>> { s: ResponseBody ->
                val parse = Jsoup.parse(s.string())
                Observable.create(ObservableOnSubscribe { e: ObservableEmitter<Document?> ->
                    e.onNext(parse)
                    e.onComplete()
                })
            })
    }

    private fun loadView(documents: ArrayList<Document>) {
        var dataList: ArrayList<BookSearchResult> = ArrayList()
        for (value in documents) {

            val img = value.select("#fmimg > img:nth-child(1)")
            val name = value.select("#info > h1:nth-child(1)")
            val author = value.select("#info > p:nth-child(2)")
            val update = value.select("#info > p:nth-child(4)")


            val bookSearchResult = BookSearchResult()
            bookSearchResult.img = searchBaseUrl + img.attr("src")
            bookSearchResult.name = name.text()
            bookSearchResult.author = author.text()
            bookSearchResult.updateTime = update.text()

            dataList.add(bookSearchResult)
        }


        if (dataList.size > 0) {
            history.visibility = View.GONE
            adapter!!.setNewData(dataList)
        } else {
            history.visibility = View.VISIBLE
        }
    }

    inner class Adapter(res: Int) : BaseQuickAdapter<BookSearchResult, BaseViewHolder>(res) {
        override fun convert(helper: BaseViewHolder, item: BookSearchResult) {
            helper.setText(R.id.name, item.name)
            helper.setText(R.id.author, item.author)
            helper.setText(R.id.time, item.updateTime)
            val img = helper.getView<ImageView>(R.id.img)
            GlideUtils.load(this@SearchActivity, item.img, img)
        }
    }


    private fun clearFocus(){
        name.isFocusable = false
        if (mInputMethodManager!!.isActive) {
            mInputMethodManager!!.hideSoftInputFromWindow(name.windowToken, 0)
        }
    }
}