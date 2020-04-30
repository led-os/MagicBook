package com.key.magicbook.activity.search

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allen.library.interceptor.Transformer
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.bookdetail.BookDetailActivity
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
    private var localUrls: ArrayList<String> = ArrayList()
    private var adapter: Adapter? = null
    private var mInputMethodManager: InputMethodManager? = null
    private val freeSecondUrl = ApiHelper.getFreeSecondUrlApi()
    override fun createPresenter(): SearchPresenter {
        return SearchPresenter()
    }

    override fun initView() {
        mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        setTitle(toolbar)
        initToolbar(toolbar)
        toolbar.setNavigationOnClickListener {
            controlBack()
        }
        getLocalSearchHistory()
        up.setOnClickListener {
            val fade = Fade()
            fade.duration = 1200
            TransitionManager.beginDelayedTransition(history, fade)
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

        name.setOnEditorActionListener { v, actionId, event ->
            if (actionId === EditorInfo.IME_ACTION_SEARCH) {
                val searchName = name.text.toString()
                checkLocalHistory(searchName)
                search(searchName)
                clearFocus()
                true
            }
            false

        }
        val linearLayoutManager = LinearLayoutManager(this)
        list.layoutManager = linearLayoutManager as RecyclerView.LayoutManager?
        adapter = Adapter(R.layout.item_book_search)

        list.adapter = adapter
        adapter!!.setOnItemClickListener { adapter,
                                           view,
                                           position ->
            val busMessage = BusMessage<Document>()
            busMessage.target = BookDetailActivity::class.java.simpleName
            val arrayList = adapter.data as ArrayList<BookSearchResult>
            busMessage.specialMessage = arrayList[position].name
            busMessage.message = arrayList[position].bookUrl
            busMessage.data = arrayList[position].data
            sendBusMessage(busMessage = busMessage)
            startActivity(Intent(this@SearchActivity, BookDetailActivity::class.java))
            overridePendingTransition(0, 0)
        }
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
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
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
            if (mInputMethodManager!!.isActive) {
                mInputMethodManager!!.showSoftInput(name, InputMethodManager.SHOW_FORCED)
            }
            if (history.visibility != View.VISIBLE) {
                val fade = Fade()
                fade.duration = 1000
                TransitionManager.beginDelayedTransition(history, fade)
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
            .flatMap(Function<Document, ObservableSource<ArrayList<BookSearchResult>>> {
                val select = it!!.select(" span:nth-child(2) > a:nth-child(1)")
                val select1 = it!!.select(" span:nth-child(4)")
                var zipWith:ArrayList<BookSearchResult>  = ArrayList()


                for ((index, value) in select.withIndex()) {
                    val bookSearchResult = BookSearchResult()
                    bookSearchResult.name = value.text()
                    bookSearchResult.author = select1[index + 1].text()
                    bookSearchResult.bookUrl = value.attr("href")
                    Log.e("pile",value.attr("href"))
                    Log.e("pile",value.text())
                    Log.e("pile", select1[index + 1].text())
                    zipWith!!.add(bookSearchResult)
                }



                Observable.create { observableEmitter ->
                    observableEmitter.onNext(zipWith)
                    observableEmitter.onComplete()
                }


            })
            .compose(Transformer.switchSchedulers())
            .subscribe(object :
                CustomBaseObserver<ArrayList<BookSearchResult>>(LoadingView(this@SearchActivity)) {
                override fun next(o: ArrayList<BookSearchResult>) {
                    if (o!!.size > 0) {
                        history.visibility = View.GONE
                        list.visibility = View.VISIBLE
                        adapter!!.setNewData(o)
                    } else {
                        history.visibility = View.VISIBLE
                    }
                }


                override fun onError(e: Throwable) {
                    super.onError(e)
                    findViewById<EditText>(R.id.name).setText("")
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




    inner class Adapter(res: Int) : BaseQuickAdapter<BookSearchResult, BaseViewHolder>(res) {
        override fun convert(helper: BaseViewHolder, item: BookSearchResult) {
            helper.setText(R.id.name, item.name)
            helper.setText(R.id.author, item.author)

            GlideUtils.loadGif(context, helper.getView<ImageView>(R.id.img))
            helper.setImageDrawable(R.id.img,context.getDrawable(R.drawable.bg_f2))

            if( item.img == null){
                JsoupUtils.getFreeDocument("https://www.dingdiann.com/" + item.bookUrl)
                    .subscribe {
                        item.data = it
                        item.img = "https://www.dingdiann.com/" + it.select("#fmimg > img").attr("src")
                        GlideUtils.load(
                            context,
                            "https://www.dingdiann.com/" + it.select("#fmimg > img").attr("src") ,
                            helper.getView<ImageView>(R.id.img)
                        )
                        item.updateTime = it.select("#info > p:nth-child(4)").text()
                        helper.setText(R.id.time, item.updateTime)
                    }
            }else{
                GlideUtils.load(
                    context,
                    item.img,
                    helper.getView<ImageView>(R.id.img)
                )
                helper.setText(R.id.time, item.updateTime)
            }

        }
    }


    private fun clearFocus() {
        name.isFocusable = false
        if (mInputMethodManager!!.isActive) {
            mInputMethodManager!!.hideSoftInputFromWindow(name.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        controlBack()
    }

    private fun controlBack() {
        if (history.visibility == View.GONE && list.visibility == View.VISIBLE && adapter!!.data.size > 0) {
            list.visibility = View.GONE
            history.visibility = View.VISIBLE
        } else {
            finish()
            overridePendingTransition(0, 0)
        }
    }
}