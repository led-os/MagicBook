package com.key.magicbook.activity.index.bookrack

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.bookdetail.BookDetailActivity
import com.key.magicbook.activity.index.IndexActivity
import com.key.magicbook.activity.index.booktype.BookTypePresenter
import com.key.magicbook.activity.read.ReadActivity
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.MineBaseFragment
import com.key.magicbook.bean.UserInfo
import com.key.magicbook.db.BookDetail
import com.key.magicbook.db.BookLike
import com.key.magicbook.db.BookRank
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.GlideUtils
import com.key.magicbook.widget.HomeRefreshHeader
import kotlinx.android.synthetic.main.fragment_index_mine_book.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.litepal.LitePal
import java.lang.Exception


/**
 * created by key  on 2020/3/2
 */
class BookRackFragment : MineBaseFragment<BookRackPresenter>() {
    private var mineBookAdapter: MineBookAdapter? = null
    private var rankListAdapter: RankListAdapter? = null
    private var rankListContentAdapter: RankListContentAdapter? = null


    override fun setLayoutId(): Int {
        return R.layout.fragment_index_mine_book
    }

    companion object {
        fun newInstance(): BookRackFragment {
            val bookRackFragment =
                BookRackFragment()
            val bundle = Bundle()
            bookRackFragment.arguments = bundle
            return bookRackFragment
        }
    }

    override fun initView() {
        setTitle(list)
        refresh.setEnableRefresh(true)
        refresh.setEnableLoadMore(false)
        refresh.setRefreshHeader(HomeRefreshHeader(activity))
        refresh.setOnRefreshListener {
            hintContent()
        }

        presenter!!.parseBookRank("https://www.dingdiann.com/ddkph.html")
        val layoutManager = GridLayoutManager(activity, 3)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        list.layoutManager = layoutManager


        val rankListManager = LinearLayoutManager(activity)
        recycler_list.layoutManager = rankListManager
        rankListAdapter = RankListAdapter()
        recycler_list.adapter = rankListAdapter

        rankListAdapter!!.setOnItemClickListener { adapter, view, position ->
            val hashMap = adapter.data[position] as HashMap<String, String>
            val arrayList = ArrayList<BookRank>()
            choose_type.text = hashMap["name"]
            val toInt = hashMap["size"]!!.toInt()
            for (book in 0 until toInt) {
                val name = book.toString() + "name"
                val url = book.toString() + "url"
                val bookRank = BookRank()
                bookRank.bookName = hashMap[name]!!
                bookRank.bookUrl = hashMap[url]!!
                bookRank.baseUrl = ConstantValues.BASE_URL
                val find =
                    LitePal.where("bookName = ? and bookUrl = ?", hashMap[name]!!, hashMap[url]!!)
                        .find(BookRank::class.java)
                if(find.size  == 0){
                    bookRank.save()
                }
                arrayList.add(bookRank)
            }

            rankListContentAdapter!!.setNewData(arrayList)
        }

        val rankListContentManager = LinearLayoutManager(activity)
        rank_list_content.layoutManager = rankListContentManager
        rankListContentAdapter = RankListContentAdapter()
        rank_list_content.adapter = rankListContentAdapter


        rankListContentAdapter!!.setOnItemClickListener {
                adapter, view, position ->
            val bookRank = adapter.data[position] as BookRank
            val exitBookDetail = presenter!!.getExitBookDetail(
                bookRank.bookName,
                ConstantValues.BASE_URL, bookRank.bookUrl
            )
            if(exitBookDetail.isNotEmpty()){
                goBookDetail(exitBookDetail[0])
            }else{
                Toast.makeText(activity,"请等待书本加载完成！！！",Toast.LENGTH_SHORT).show()
            }
        }

        mineBookAdapter = MineBookAdapter(R.layout.item_book_mine)
        list.adapter = mineBookAdapter
        mineBookAdapter!!.setEmptyView(UiUtils.inflate(activity, R.layout.no_data))
        mineBookAdapter!!.setOnItemClickListener { adapter,
                                                   _,
                                                   position ->
            val busMessage = BusMessage<BookDetail>()
            val item = adapter.getItem(position) as BookLike
            busMessage.data = getBookDetail(item)
            busMessage.message = ""
            busMessage.target = ReadActivity::class.java.simpleName
            busMessage.specialMessage = "0"
            EventBus.getDefault().postSticky(busMessage)
            startActivity(Intent(activity, ReadActivity::class.java))
            activity!!.overridePendingTransition(0, 0)
        }
        loadData()
        back.setOnClickListener {
            showContent()
        }
    }

    private fun loadData() {
        val find =
            LitePal.where("isBookCase = ? and userName = ?", "true", getUserInfo().userName)
                .find(BookLike::class.java)
        if (find.size > 0) {
            mineBookAdapter!!.setNewData(find)
        }
    }

    inner class MineBookAdapter(res: Int) : BaseQuickAdapter<BookLike, BaseViewHolder>(res) {
        override fun convert(helper: BaseViewHolder, item: BookLike) {
            helper.setText(R.id.book_name, item.bookName)
            GlideUtils.load(activity, item.bookCover, helper.getView(R.id.book_cover))
        }
    }

    inner class RankListAdapter() :
        BaseQuickAdapter<HashMap<String, String>, BaseViewHolder>(R.layout.item_book_rank_list) {
        override fun convert(helper: BaseViewHolder, item: HashMap<String, String>) {
            helper.setText(R.id.book_type, item["name"])
        }
    }


    inner class RankListContentAdapter() :
        BaseQuickAdapter<BookRank, BaseViewHolder>(R.layout.item_book_rank_list_content) {
        override fun convert(helper: BaseViewHolder, item: BookRank) {
            GlideUtils.loadGif(context, helper.getView(R.id.image))
            helper.setText(R.id.name, item.bookName)
            helper.setText(R.id.author, "")
            helper.setText(R.id.intro, "")

            if (item.bookDetail == null && !item.isLoad) {
                item.isLoad = true
                try {
                    val exitBookDetail = presenter!!.getExitBookDetail(
                        item.bookName,
                        ConstantValues.BASE_URL,
                        item.bookUrl
                    )
                    if (exitBookDetail.isEmpty()) {
                        JsoupUtils.getFreeDocumentForBody(ConstantValues.BASE_URL + item.bookUrl)
                            .subscribe(object : CustomBaseObserver<ResponseBody>() {
                                override fun next(o: ResponseBody?) {
                                    try {
                                        val parse = Jsoup.parse(o!!.string())
                                        val parseDocument = presenter!!.parseDocument(parse)

                                        item.bookDetail = parseDocument
                                        parseDocument.bookUrl = item.bookUrl
                                        parseDocument.isLooked = "false"
                                        parseDocument.isBookCase = "false"
                                        item.bookCover = parseDocument.bookCover
                                        item.bookIntro = parseDocument.bookIntro
                                        item.bookAuthor = parseDocument.bookAuthor
                                        parseDocument.save()
                                        GlideUtils.load(
                                            context,
                                            parseDocument.bookCover,
                                            helper.getView(R.id.image)
                                        )
                                        helper.setText(R.id.intro, item.bookIntro)
                                        helper.setText(R.id.author, item.bookAuthor)
                                    } catch (e: Exception) {
                                        item.isLoad = false
                                    }
                                }

                            })
                    } else {
                        item.bookCover = exitBookDetail[0].bookCover
                        item.bookIntro = exitBookDetail[0].bookIntro
                        item.bookAuthor = exitBookDetail[0].bookAuthor
                        helper.setText(R.id.author, item.bookAuthor)
                        GlideUtils.load(
                            context,
                            exitBookDetail[0].bookCover,
                            helper.getView(R.id.image)
                        )
                        helper.setText(R.id.intro, item.bookIntro)
                    }

                } catch (e: Exception) {
                    item.isLoad = false
                }
            } else {
                GlideUtils.load(
                    context,
                    item.bookCover,
                    helper.getView(R.id.image)
                )
                helper.setText(R.id.intro, item.bookIntro)
                helper.setText(R.id.author, item.bookAuthor)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments
    }

    private fun getUserInfo(): UserInfo {
        val findAll = LitePal.findAll(UserInfo::class.java)
        for (value in findAll) {
            if (value.isLogin == "true") {
                return value
            }
        }
        return UserInfo()
    }


    private fun showContent() {
        refresh.finishRefresh()
        val indexActivity = activity as IndexActivity
        indexActivity.show()
        val va: ObjectAnimator = ObjectAnimator.ofFloat(
            refresh,
            "translationY",
            refresh.y,
            0f
        )
        va.interpolator = DecelerateInterpolator()
        va.duration = 500

        va.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                back.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
        })
        va.start()

        list_rank.visibility = View.GONE

    }

    private fun hintContent() {
        val indexActivity = activity as IndexActivity
        indexActivity.hide()
        val va: ObjectAnimator = ObjectAnimator.ofFloat(
            refresh,
            "translationY",
            refresh.y,
            UiUtils.getScreenWidth(activity).toFloat() * 2
        )
        va.interpolator = DecelerateInterpolator()
        va.duration = 500
        va.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                list_rank.visibility = View.VISIBLE
                back.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}

        })
        va.start()


    }

    private fun getBookDetail(bookLike: BookLike): BookDetail {
        val find = LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ?",
            bookLike.bookName, bookLike.baseUrl, bookLike.bookUrl
        ).find(BookDetail::class.java)
        return if (find.size > 0) {
            find[0];
        } else {
            BookDetail()
        }
    }

    override fun createPresenter(): BookRackPresenter {
        return BookRackPresenter()
    }


    fun parseBookRack(list: ArrayList<HashMap<String, String>>) {

        val rankNames = ArrayList<String>()
        val arrayList = ArrayList<BookRank>()

        for ((index, value) in list.withIndex()) {
            rankNames.add(value["name"]!!)
            val toInt = value["size"]!!.toInt()
            if (index == 0) {
                for (book in 0 until toInt) {
                    val name = book.toString() + "name"
                    val url = book.toString() + "url"
                    val bookRank = BookRank()
                    bookRank.bookName = value[name]!!
                    bookRank.bookUrl = value[url]!!
                    arrayList.add(bookRank)
                }
            }

            for (book in 0 until toInt) {
                val name = book.toString() + "name"
                val url = book.toString() + "url"
                val bookRank = BookRank()
                bookRank.bookName = value[name]!!
                bookRank.bookUrl = value[url]!!
                bookRank.baseUrl = ConstantValues.BASE_URL
                val find =
                    LitePal.where("bookName = ? and bookUrl = ?", value[name]!!, value[url]!!)
                        .find(BookRank::class.java)
                if(find.size  == 0){
                    bookRank.save()
                }
            }
        }


        choose_type.text = rankNames[0]
        rankListContentAdapter!!.setNewData(arrayList)
        rankListAdapter!!.setNewData(list)

    }

    private fun goBookDetail(bookDetail: BookDetail){
        val busMessage = BusMessage<Document>()
        busMessage.target = BookDetailActivity::class.java.simpleName
        busMessage.specialMessage = bookDetail.bookName
        busMessage.message = bookDetail.bookUrl
        EventBus.getDefault().postSticky(busMessage)
        startActivity(Intent(activity, BookDetailActivity::class.java))
    }


    override fun receiveMessage(busMessage: BusMessage<Any>) {
        super.receiveMessage(busMessage)
        if(busMessage.message != null ){
            if(busMessage.message == "bookRefresh"){
                activity!!.runOnUiThread {
                    loadData()
                }
            }
        }
    }
}




