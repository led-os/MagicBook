package com.key.magicbook.activity.index

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.allen.library.interceptor.Transformer
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator
import com.bigkoo.convenientbanner.holder.Holder
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.base.BaseFragment
import com.key.keylibrary.base.GlobalApplication.context
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.GlideUtils
import kotlinx.android.synthetic.main.fragment_book_type.*
import kotlinx.android.synthetic.main.iitem_book_type_header.*
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/5/1
 */
class BookTypeFragment : BaseFragment() {
    private var mBookUrl = ""
    private var adapter:Adapter ?= null
    private var document:Document ?= null
    private var bookDetails :ArrayList<BookDetail> ?= null
    private var headers :ArrayList<BookDetail> ?= null
    private var convenientBanner :ConvenientBanner<BookDetail> ?= null
    override fun setLayoutId(): Int {
        return R.layout.fragment_book_type
    }

    companion object{
        private const val BOOK_URL = "book_url"
        fun newInstance(url :String):BookTypeFragment{
            val bookTypeFragment = BookTypeFragment()
            val bundle = Bundle()
            bundle.putString(BOOK_URL,url)
            bookTypeFragment.arguments = bundle
            return bookTypeFragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBookUrl = arguments!!.getString(BOOK_URL)!!
    }
    override fun initView() {
        list.layoutManager = LinearLayoutManager(context)
        adapter = Adapter()
        list.adapter = adapter
    }


    public class Adapter() :BaseQuickAdapter<BookDetail,BaseViewHolder>(R.layout.item_book_type_list){
        override fun convert(helper: BaseViewHolder, item: BookDetail) {

        }
    }



    public class TypeHolder(itemView: View?,var activity : FragmentActivity) : Holder<BookDetail>(itemView) {
        override fun updateUI(data: BookDetail?) {
            itemView.findViewById<TextView>(R.id.name).text = data!!.bookName
//            itemView.findViewById<TextView>(R.id.author).text = data!!.bookAuthor
//            itemView.findViewById<TextView>(R.id.intro).text = data!!.bookIntro
            GlideUtils.load(
                activity,
                data!!.bookCover ,
                itemView.findViewById<ImageView>(R.id.image)
            )
        }

        override fun initView(itemView: View?) {

        }
    }

    override fun onVisibleChanged(isVisible: Boolean) {
        super.onVisibleChanged(isVisible)
        if(isVisible){
            if(bookDetails == null){
                loadData(false)
            }
        }
    }

    private fun loadData(isRefresh :Boolean){
        JsoupUtils.getFreeDocument(mBookUrl)
            .compose(Transformer.switchSchedulers())
            .subscribe(object : CustomBaseObserver<Document>() {
                override fun next(o: Document?) {
                    document = o
                    headers = ArrayList()
                    val select = o!!.select("#hotcontent > div > div")
                    for(value in select){
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
                            headers!!.add(bookDetail)
                        }
                    }

                    val new = o!!.select("#newscontent > div.r > ul > li")
                     bookDetails = ArrayList<BookDetail>()
                    for(value in new){
                        val bookName =  value.select("span.s2 > a").text()
                        val attr = value.select("span.s2 > a")
                            .attr("href")
                        val authorName = value.select("span.s5").text()
                        val bookDetail = BookDetail()
                        bookDetail.bookName = bookName
                        bookDetail.bookUrl = attr
                        bookDetail.bookAuthor = authorName
                        bookDetail.bookType = ""
                        bookDetails!!.add(bookDetail)
                    }
                    setData()

                }
            })
    }

    private fun setData(){
        val headerView =
            layoutInflater.inflate(R.layout.iitem_book_type_header, null)
        headerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            UiUtils.dip2px(200f)
        )
        convenientBanner = headerView.findViewById(R.id.convenientBanner)
        convenientBanner!!.setPages(
            object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<BookDetail> {
                    return TypeHolder(itemView,activity!!)
                }

                override fun getLayoutId(): Int {
                    return R.layout.item_book_type_banner
                }

            },headers)
        adapter!!.addHeaderView(headerView)
        adapter!!.setNewData(bookDetails!!)

    }
}