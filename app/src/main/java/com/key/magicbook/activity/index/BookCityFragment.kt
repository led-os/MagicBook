package com.key.magicbook.activity.index

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.allen.library.interceptor.Transformer
import com.key.keylibrary.base.BaseFragment
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.search.SearchActivity
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.GlideUtils
import com.stone.pile.libs.PileLayout
import kotlinx.android.synthetic.main.fragment_index_book_city.*
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/3/2
 */
class BookCityFragment : BaseFragment() {
    private val baseUrl = "https://www.dingdiann.com/"
    private var books: ArrayList<BookDetail> = ArrayList()
    override fun setLayoutId(): Int {
        return R.layout.fragment_index_book_city
    }

    override fun initView() {

        search.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
            activity!!.overridePendingTransition(0, 0)
        }
        loadPile()
    }


    private fun initData() {
        JsoupUtils.getFreeDocument("https://www.dingdiann.com")
            .compose(Transformer.switchSchedulers())
            .subscribe(object : CustomBaseObserver<Document>(LoadingView(activity)) {
                override fun next(o: Document?) {
                    books = ArrayList()
                    val select = o!!.select("#hotcontent > div.l > div")
                    for (value in select) {
                        val bookDetail = BookDetail()
                        val img =
                            value.select("div.image > a > img")
                                .attr("src")
                        val name =
                            value.select("dl > dt > a")
                                .text()
                        val url =
                            value.select("dl > dt > a")
                                .attr("href")
                        val intro = value.select("dl > dd").text()
                        bookDetail.bookIntro = intro
                        bookDetail.bookCover = img
                        bookDetail.bookName = name
                        bookDetail.bookUrl = url
                        if (!TextUtils.isEmpty(intro)) {
                            books!!.add(bookDetail)
                        }
                    }
                    val select1 = o!!.select("#novelslist1 > div")
                    for (value in select1) {
                        val bookDetail = BookDetail()

                        val img = value.select(" div > div.image > img").attr("src")
                        val name = value.select(" div > dl > dt > a").text()
                        val url = value.select(" div > dl > dt > a").attr("href")
                        val intro = value.select("div > dl > dd").text()

                        bookDetail.bookIntro = intro
                        bookDetail.bookCover = img
                        bookDetail.bookName = name
                        bookDetail.bookUrl = url

                        if (!TextUtils.isEmpty(intro)) {
                            books!!.add(bookDetail)
                        }

                    }


                    val select2 = o!!.select("#novelslist2 > div")
                    for (value in select2) {
                        val bookDetail = BookDetail()
                        val img = value.select(" div > div.image > img").attr("src")
                        val name = value.select(" div > dl > dt > a").text()
                        val url = value.select(" div > dl > dt > a").attr("href")
                        val intro = value.select("div > dl > dd").text()

                        bookDetail.bookIntro = intro
                        bookDetail.bookCover = img
                        bookDetail.bookName = name
                        bookDetail.bookUrl = url
                        if (!TextUtils.isEmpty(intro)) {
                            books!!.add(bookDetail)
                        }
                    }
                    pile_layout.notifyDataSetChanged()
                    pile_layout.invalidate()

                }
            })
    }


    private fun loadPile() {
        pile_layout.layoutParams.height = UiUtils.getScreenHeight(activity) / 5
        val value = object : PileLayout.Adapter() {
            override fun getItemCount(): Int {
                return if(books.size > 0){
                    books!!.size
                }else{
                    1
                }

            }

            override fun getLayoutId(): Int {
                return R.layout.item_city_book_banner
            }

            override fun bindView(view: View?, index: Int) {
               if(books.size > 0){
                   var viewHolder: ViewHolder? = null
                   if (view != null) {
                       if (view!!.tag != null && viewHolder != null) {
                           viewHolder = view!!.tag as ViewHolder
                       } else {
                           viewHolder = ViewHolder()
                           viewHolder.imageView =
                               view.findViewById(R.id.image) as ImageView
                           view.tag = viewHolder
                       }
                   }

                   if(index >= 0 && index <books.size){
                       Log.e("book222",baseUrl + books!![index].bookCover)
                       GlideUtils.load(
                           activity,
                           baseUrl + books!![index].bookCover,
                           viewHolder!!.imageView
                       )
                   }

               }
            }


        }
        pile_layout.adapter = value


    }

    internal class ViewHolder {
        var imageView: ImageView? = null
    }


    override fun onVisibleChanged(isVisible: Boolean) {
        super.onVisibleChanged(isVisible)
        if (isVisible) {
            initData()
        }

    }

}