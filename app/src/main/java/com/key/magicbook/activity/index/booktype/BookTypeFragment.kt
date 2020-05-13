package com.key.magicbook.activity.index.booktype

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator
import com.bigkoo.convenientbanner.holder.Holder
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.MineBaseFragment
import com.key.magicbook.db.BookDetail
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.GlideUtils
import kotlinx.android.synthetic.main.fragment_book_type.*
import okhttp3.ResponseBody
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
    private var convenientBanner: ConvenientBanner<BookDetail>? = null

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
        list.adapter = adapter
    }


    class Adapter() : BaseQuickAdapter<BookDetail, BaseViewHolder>(R.layout.item_book_type_list) {
        override fun convert(helper: BaseViewHolder, item: BookDetail) {
            GlideUtils.loadGif(context, helper.getView<ImageView>(R.id.image))
            helper.setText(R.id.name, item.bookName)
            helper.setText(R.id.author, item.bookAuthor)
            if (item.bookCover == null && !item.isLoad) {
                    item.isLoad = true
                try {
                    JsoupUtils.getFreeDocumentForBody(ConstantValues.BASE_URL + item.bookUrl)
                        .subscribe(object :CustomBaseObserver<ResponseBody>(){
                            override fun next(o: ResponseBody?) {
                                try {
                                    val parse = Jsoup.parse(o!!.string())
                                    item.bookCover =
                                        ConstantValues.BASE_URL + parse.select("#fmimg > img").attr("src")
                                    GlideUtils.load(
                                        context,
                                        ConstantValues.BASE_URL+ parse.select("#fmimg > img")
                                            .attr("src"),
                                        helper.getView<ImageView>(R.id.image)
                                    )
                                    item.bookIntro = parse.select("#intro").text()
                                    helper.setText(R.id.intro, item.bookIntro)
                                }catch (e :Exception){
                                    item.isLoad = false
                                }
                            }

                        })
                }catch (e :Exception){
                    item.isLoad = false
                }
            } else {
                GlideUtils.load(
                    context,
                    item.bookCover,
                    helper.getView<ImageView>(R.id.image)
                )
                helper.setText(R.id.intro, item.bookIntro)
            }
        }
    }


    class TypeHolder(itemView: View?, var activity: FragmentActivity) :
        Holder<BookDetail>(itemView) {
        override fun updateUI(data: BookDetail?) {
            itemView.findViewById<TextView>(R.id.name).text = data!!.bookName
            itemView.findViewById<ConstraintLayout>(R.id.banner).layoutParams.width =
                UiUtils.getScreenWidth(activity)  -UiUtils.dip2px(36f)


            GlideUtils.load(
                activity,
                ConstantValues.BASE_URL + data!!.bookCover,
                itemView.findViewById<ImageView>(R.id.image)
            )

            Thread {
                GlideUtils.loadBlur(
                    activity,
                    ConstantValues.BASE_URL + data!!.bookCover,
                    itemView.findViewById<ImageView>(R.id.bg)
                )
            }.start()

        }

        override fun initView(itemView: View?) {

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
        convenientBanner = headerView.findViewById(R.id.convenientBanner)
        convenientBanner!!.setPages(
            object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<BookDetail> {
                    return TypeHolder(
                        itemView,
                        activity!!
                    )
                }

                override fun getLayoutId(): Int {
                    return R.layout.item_book_type_banner
                }

            }, headers
        )
        adapter!!.addHeaderView(headerView)
        adapter!!.setNewData(bookDetails!!)

    }

    override fun createPresenter(): BookTypePresenter {
        return BookTypePresenter()
    }
}