package com.key.magicbook.activity.index

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allen.library.interceptor.Transformer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.base.BaseFragment
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.search.SearchActivity
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.GlideUtils
import com.stone.pile.libs.PileLayout
import kotlinx.android.synthetic.main.fragment_index_book_city.*
import kotlinx.android.synthetic.main.fragment_index_book_city.list
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/3/2
 */
class BookCityFragment : BaseFragment() {
    private val baseUrl = "https://www.dingdiann.com/"
    private var adapter :Adapter ?= null
    private var books: ArrayList<BookDetail> = ArrayList()
    private var document :Document ?= null
    private var mPileLayout:PileLayout ?= null
    private var headerView :View ?= null
    override fun setLayoutId(): Int {
        return R.layout.fragment_index_book_city
    }

    companion object {
        fun  newInstance():BookCityFragment{
            val bookCityFragment = BookCityFragment()
            val bundle = Bundle()
            bookCityFragment.arguments = bundle
            return bookCityFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments
    }
    override fun initView() {
        search.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
            activity!!.overridePendingTransition(0, 0)
        }
        list.layoutManager = LinearLayoutManager(activity)
        adapter = Adapter()
        list.adapter = adapter
        headerView = UiUtils.inflate(activity,R.layout.item_book_city_head)
        mPileLayout = headerView!!.findViewById(R.id.pile_layout)
        adapter!!.addHeaderView(headerView!!)
//        scroll.setOnTouchMoveListener { code, touchY,transY ->
//          var interceptor  = false
//          when(code){
//              0->{
//                  val stateBar = UiUtils.getStateBar(activity)
//                  val b1 = UiUtils.location(list)[1] <= stateBar
//                  interceptor = !b1
//                  if(b1){
//                      val linearLayoutManager = list.layoutManager as LinearLayoutManager
//                      val lastPosition =
//                          linearLayoutManager.findLastVisibleItemPosition()
//
//                      val firstPosition =
//                          linearLayoutManager.findFirstVisibleItemPosition()
//                      interceptor = if(transY < 0){
//                          lastPosition >= adapter!!.data.size - 1
//                      }else{
//                          firstPosition == 0;
//                      }
//
//                  }
//
//              }
//              1->{
//                  interceptor = false
//              }
//              2->{
//                  interceptor  = false
//              }
//          }
//          interceptor
//      }
        initData()
    }


    private fun initData() {
        JsoupUtils.getFreeDocument("https://www.dingdiann.com")
            .compose(Transformer.switchSchedulers())
            .subscribe(object : CustomBaseObserver<Document>() {
                override fun next(o: Document?) {
                    books = ArrayList()
                    document = o
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


                    //#novelslist1 > div:nth-child(1) > ul

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



                    loadPile()
                    Thread(Runnable {
                        loadNovelList()
                    }).start()



                }
            })
    }


    private fun loadPile() {
        if(mPileLayout != null){
            if(mPileLayout!!.adapter == null){
                mPileLayout!!.setAdapter(object :PileLayout.Adapter(){
                    override fun getItemCount(): Int {
                        return if(books.size > 0){
                            books!!.size
                        }else{
                            0
                        }

                    }

                    override fun getLayoutId(): Int {
                        return R.layout.item_city_book_banner
                    }

                    override fun bindView(view: View?, index: Int) {
                        if (view != null){
                            var viewHolder: ViewHolder  ?= null
                            if(view!!.tag != null){
                                viewHolder = view!!.tag as ViewHolder
                            }else{
                                viewHolder = ViewHolder()
                                viewHolder.imageView =
                                    view.findViewById(R.id.image) as ImageView

                                view.tag = viewHolder
                            }

                            if(index >= 0 && index <books.size){
                                GlideUtils.load(
                                    activity,
                                    baseUrl + books!![index].bookCover,
                                    viewHolder!!.imageView
                                )
                            }
                        }

                    }


                    override fun onItemClick(view: View?, position: Int) {
                        super.onItemClick(view, position)
                    }

                    override fun displaying(position: Int) {
                        super.displaying(position)
                        headerView!!.findViewById<TextView>(R.id.name).text = books!![position].bookName
                        headerView!!.findViewById<TextView>(R.id.intro).text = books!![position].bookIntro
                    }

                })
            }
        }

    }

    internal class ViewHolder {
        var imageView: ImageView? = null
    }



    inner class Adapter() :BaseQuickAdapter<ArrayList<BookDetail>,BaseViewHolder>(R.layout.item_fragment_city){
        override fun convert(helper: BaseViewHolder, item: ArrayList<BookDetail>) {
            if(item.size > 0){
                helper.setText(R.id.type,item[0].bookType)
                val view = helper.getView<RecyclerView>(R.id.item_list)
                val linearLayoutManager = LinearLayoutManager(context)
                linearLayoutManager.orientation =  RecyclerView.HORIZONTAL
                view.layoutManager = linearLayoutManager
                val itemAdapter = ItemAdapter()
                view.adapter = itemAdapter
                itemAdapter!!.setNewData(item)
            }

        }

    }



     class ItemAdapter:BaseQuickAdapter<BookDetail,BaseViewHolder>(R.layout.item_fragment_city_list){
        override fun convert(helper: BaseViewHolder, item: BookDetail) {
            helper.setText(R.id.name,item.bookName)
            GlideUtils.loadGif(context, helper.getView<ImageView>(R.id.image))
            if( item.bookCover == null){
                JsoupUtils.connectFreeUrl("https://www.dingdiann.com/"+item.bookUrl ,"#fmimg > img:nth-child(1)")
                    .subscribe {
                        item.bookCover = "https://www.dingdiann.com/" +it.attr("src")
                        GlideUtils.load(
                            context,
                            "https://www.dingdiann.com/" +it.attr("src") ,
                            helper.getView<ImageView>(R.id.image)
                        )
                    }
            }else{
                GlideUtils.load(
                    context,
                    item.bookCover ,
                    helper.getView<ImageView>(R.id.image)
                )
            }


        }

        override fun onViewRecycled(holder: BaseViewHolder) {
            super.onViewRecycled(holder)
        }
    }
    private fun loadNovelList(){
        val arrayList = ArrayList<ArrayList<BookDetail>>()
        if(document != null){
            //经典推荐
            val scriptures = document!!.select("#hotcontent > div.r > ul > li")
            var scripturesDetails =ArrayList<BookDetail>()
            for(value in scriptures){

                val bookName =  value.select("span.s2 > a").text()
                val attr = value.select("span.s2 > a")
                    .attr("href")
                val authorName = value.select("span.s5").text()
                val bookDetail = BookDetail()
                bookDetail.bookName = bookName
                bookDetail.bookUrl = attr
                bookDetail.bookAuthor = authorName
                bookDetail.bookType = "经典推荐"
                scripturesDetails.add(bookDetail)

            }
            arrayList.add(scripturesDetails)

            //0 ：玄幻奇幻 1 ：武侠仙侠 2 :都市言情
            val select = document!!.select("#novelslist1 > div")
            var index = 0
            for(value in select){
                val select1 = value.select("ul > li")

                val bookDetails = ArrayList<BookDetail>()
                for(value in select1){
                    val all = value.text()
                    val bookName = value.select("a").text()
                    val attr = value.select("a").attr("href")
                    val authorName = all.replace(" ", "")
                        .replace("/", "").replace(bookName, "")
                    val bookDetail = BookDetail()
                    bookDetail.bookName = bookName
                    bookDetail.bookUrl = attr
                    bookDetail.bookAuthor = authorName
                    when(index){
                        0->{
                            bookDetail.bookType = "玄幻奇幻"
                            bookDetails.add(bookDetail)
                        }
                        1->{
                            bookDetail.bookType = "武侠仙侠"
                            bookDetails.add(bookDetail)
                        }
                        2->{
                            bookDetail.bookType = "都市言情"
                            bookDetails.add(bookDetail)
                        }
                    }
                }
                index++
                if(bookDetails.size > 0){
                    arrayList.add(bookDetails)
                }
            }

            //0 ：历史军事 1 ：科幻灵异 2 :网游竞技
            val select2 = document!!.select("#novelslist2 > div")
            index = 0
            for(value in select2){
                val select1 = value.select("ul > li")
                val bookDetailNovel = ArrayList<BookDetail>()
                for(value in select1){
                    val all = value.text()
                    val bookName = value.select("a").text()
                    val attr = value.select("a").attr("href")
                    val authorName = all.replace(" ", "")
                        .replace("/", "").replace(bookName, "")
                    val bookDetail = BookDetail()
                    bookDetail.bookName = bookName
                    bookDetail.bookUrl = attr
                    bookDetail.bookAuthor = authorName
                    when(index){
                        0->{
                            bookDetail.bookType = "历史军事"
                            bookDetailNovel.add(bookDetail)
                        }
                        1->{
                            bookDetail.bookType = "科幻灵异"
                            bookDetailNovel.add(bookDetail)
                        }
                        2->{
                            bookDetail.bookType = "网游竞技"
                            bookDetailNovel.add(bookDetail)
                        }
                    }
                }

                index++
                if(bookDetailNovel.size > 0){
                    arrayList.add(bookDetailNovel)
                }
            }

            //新书
            val new = document!!.select("#newscontent > div.r > ul > li")
            val bookDetails = ArrayList<BookDetail>()
            for(value in new){
                val bookName =  value.select("span.s2 > a").text()
                val attr = value.select("span.s2 > a").attr("href")
                val authorName = value.select("span.s5").text()
                val bookDetail = BookDetail()
                bookDetail.bookName = bookName
                bookDetail.bookUrl = attr
                bookDetail.bookAuthor = authorName
                bookDetail.bookType = "新书"
                bookDetails.add(bookDetail)
            }
            arrayList.add(bookDetails)

            if(activity != null){
                activity!!.runOnUiThread {
                    adapter!!.setNewData(arrayList)
                }
            }

        }
    }

}