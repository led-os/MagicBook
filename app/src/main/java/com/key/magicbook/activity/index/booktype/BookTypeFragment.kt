package com.key.magicbook.activity.index.booktype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.bookdetail.BookDetailActivity
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.MineBaseFragment
import com.key.magicbook.db.BookDetail
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.GlideUtils
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.adapter.OnPageChangeListenerAdapter
import com.zhpan.bannerview.constants.IndicatorGravity
import com.zhpan.bannerview.constants.IndicatorSlideMode
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.bannerview.holder.ViewHolder
import kotlinx.android.synthetic.main.activity_book_detail.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/5/1
 */
class BookTypeFragment : MineBaseFragment<BookTypePresenter>() {
    private var mBookUrl = ""
    private var adapter: Adapter? = null
    private var document: Document? = null
    private var bookDetails: ArrayList<BookDetail>? = null
    private var headers: ArrayList<BookDetail>? = null
    private var convenientBanner: BannerViewPager<BookDetail,NetViewHolder>? = null

    override fun setLayoutId(): Int {
        return R.layout.fragment_book_type
    }

    companion object {
        private const val BOOK_URL = "book_url"
        fun newInstance(url: String): BookTypeFragment {
            val bookTypeFragment =
                BookTypeFragment()
            val bundle = Bundle()
            bundle.putString(BOOK_URL, url)
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
        adapter =
            Adapter()
        adapter!!.setOnItemClickListener { adapter, view, position ->
            goBookDetail(adapter.data[position] as BookDetail)
        }
        list.adapter = adapter
        adapter!!.setEmptyView(UiUtils.inflate(activity,R.layout.no_data))
    }


   inner  class Adapter() : BaseQuickAdapter<BookDetail, BaseViewHolder>(R.layout.item_book_type_list) {
        override fun convert(helper: BaseViewHolder, item: BookDetail) {
            GlideUtils.loadGif(context, helper.getView(R.id.image))
            helper.setText(R.id.name, item.bookName)
            helper.setText(R.id.author, item.bookAuthor)
            helper.setText(R.id.intro, "")
            if (item.bookCover == null && !item.isLoad) {
                    item.isLoad = true
                try {
                    val exitBookDetail = presenter!!.getExitBookDetail(
                        item.bookName,
                        ConstantValues.BASE_URL,
                        item.bookUrl
                    )
                    if(exitBookDetail.isEmpty()){
                        JsoupUtils.getFreeDocumentForBody(ConstantValues.BASE_URL + item.bookUrl)
                            .subscribe(object :CustomBaseObserver<ResponseBody>(){
                                override fun next(o: ResponseBody?) {
                                    try {
                                        val parse = Jsoup.parse(o!!.string())
                                        val parseDocument = presenter!!.parseDocument(parse)

                                        parseDocument.bookUrl = item.bookUrl
                                        parseDocument.isLooked = "false"
                                        parseDocument.isBookCase = "false"
                                        item.bookCover = parseDocument.bookCover
                                        item.bookIntro = parseDocument.bookIntro
                                        parseDocument.save()
                                        GlideUtils.load(
                                            context,
                                            parseDocument.bookCover,
                                            helper.getView(R.id.image)
                                        )
                                        helper.setText(R.id.intro, item.bookIntro)
                                    }catch (e :Exception){
                                        item.isLoad = false
                                    }
                                }

                            })
                    }else{
                        item.bookCover = exitBookDetail[0].bookCover
                        item.bookIntro = exitBookDetail[0].bookIntro
                        GlideUtils.load(
                            context,
                            exitBookDetail[0].bookCover,
                            helper.getView(R.id.image)
                        )
                        helper.setText(R.id.intro, item.bookIntro)
                    }

                }catch (e :Exception){
                    item.isLoad = false
                }
            } else {
                GlideUtils.load(
                    context,
                    item.bookCover,
                    helper.getView(R.id.image)
                )
                helper.setText(R.id.intro, item.bookIntro)
            }
        }
    }



    override fun onVisibleChanged(isVisible: Boolean) {
        super.onVisibleChanged(isVisible)
        if (isVisible) {
            if (bookDetails == null) {
                loadData(false)
            }
        }
    }

    private fun loadData(isRefresh: Boolean) {
        presenter!!.getTypeDocument(mBookUrl)
    }

    fun loadDocument(o :Document){
        document = o
        headers = presenter!!.parseHeaders(o)
        bookDetails =  presenter!!.parseBookDetails(o)
        setData()
    }

    private fun setData() {
        val headerView =
            layoutInflater.inflate(R.layout.iitem_book_type_header, null)
        headerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        convenientBanner = headerView.findViewById(R.id.bv_top)
        convenientBanner!!.setCanLoop(true)
            .setHolderCreator { NetViewHolder() }
            .setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
            .setOnPageChangeListener(
                object : OnPageChangeListenerAdapter() {
                    override fun onPageSelected(position: Int) {
                    }
                }
            )
            .create(headers)
        adapter!!.removeAllHeaderView()
        adapter!!.addHeaderView(headerView)
        adapter!!.setNewData(bookDetails!!)

    }

    override fun createPresenter(): BookTypePresenter {
        return BookTypePresenter()
    }


    private fun goBookDetail(bookDetail: BookDetail){
        val busMessage = BusMessage<Document>()
        busMessage.target = BookDetailActivity::class.java.simpleName
        busMessage.specialMessage = bookDetail.bookName
        busMessage.message = bookDetail.bookUrl
        EventBus.getDefault().postSticky(busMessage)
        startActivity(Intent(activity, BookDetailActivity::class.java))
    }

   inner class NetViewHolder : ViewHolder<BookDetail?> {


        override fun getLayoutId(): Int {
           return R.layout.item_book_type_banner
        }

        override fun onBind(itemView: View, data: BookDetail?, position: Int, size: Int) {
            itemView.findViewById<TextView>(R.id.name).text = data!!.bookName
            itemView.findViewById<ConstraintLayout>(R.id.banner).layoutParams.width =
                UiUtils.getScreenWidth(activity)  -UiUtils.dip2px(36f)

            GlideUtils.load(
                activity,
                ConstantValues.BASE_URL + data!!.bookCover,
                itemView.findViewById(R.id.image)
            )

            Thread {
                GlideUtils.loadBlur(
                    activity,
                    ConstantValues.BASE_URL + data!!.bookCover,
                    itemView.findViewById<ImageView>(R.id.bg)
                )
            }.start()

            itemView.findViewById<LinearLayout>(R.id.banner_root).setOnClickListener {
                goBookDetail(data)
            }
        }
    }
}