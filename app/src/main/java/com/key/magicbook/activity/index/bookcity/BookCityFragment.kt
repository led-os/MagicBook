package com.key.magicbook.activity.index.bookcity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.bookdetail.BookDetailActivity
import com.key.magicbook.activity.search.SearchActivity
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.MineBaseFragment
import com.key.magicbook.bean.BookSearchResult
import com.key.magicbook.db.BookDetail
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.GlideUtils
import com.stone.pile.libs.PileLayout
import kotlinx.android.synthetic.main.fragment_index_book_city.*
import kotlinx.android.synthetic.main.fragment_index_book_city.list
import org.greenrobot.eventbus.EventBus
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/3/2
 */
class BookCityFragment : MineBaseFragment<BookCityPresenter>() {
    private val baseUrl =ConstantValues.BASE_URL
    private var adapter : Adapter?= null
    private var mPileLayout:PileLayout ?= null
    private var headerView :View ?= null
    private var inAnimation = false
    private var searcherWidth = 0

    override fun setLayoutId(): Int {
        return R.layout.fragment_index_book_city
    }

    companion object {
        fun  newInstance(): BookCityFragment {
            val bookCityFragment =
                BookCityFragment()
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
        adapter!!.setEmptyView(UiUtils.inflate(activity,R.layout.no_data))
        headerView = UiUtils.inflate(activity,R.layout.item_book_city_head)
        mPileLayout = headerView!!.findViewById(R.id.pile_layout)
        adapter!!.addHeaderView(headerView!!)


        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy > 0){
                    if(searcherWidth == 0){
                        searcherWidth = search.width
                    }
                    if(search.visibility == View.VISIBLE && !inAnimation) {
                        val duration = ObjectAnimator
                            .ofInt(
                                ViewWrapper(
                                    search
                                ), "width", searcherWidth, 0)
                            .setDuration(300)

                        duration.addListener(object :Animator.AnimatorListener{
                            override fun onAnimationRepeat(animation: Animator?) {

                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                search.visibility = View.GONE
                                inAnimation = false
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {
                                inAnimation = true
                            }

                        })
                        duration.start()
                    }

                }else{
                    if(search.visibility == View.GONE&& !inAnimation){
                        search.layoutParams.width = 0
                        search.visibility = View.VISIBLE
                        val duration = ObjectAnimator
                            .ofInt(
                                ViewWrapper(
                                    search
                                ), "width", 0, searcherWidth)
                            .setDuration(300)
                        duration.addListener(object :Animator.AnimatorListener{
                            override fun onAnimationRepeat(animation: Animator?) {

                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                inAnimation = false
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {
                                inAnimation = true
                            }

                        })

                        duration.start()

                    }

                }
            }
        })
        initData()
    }


     fun setDocument(document: Document){
        presenter!!.loadPile(document)
        val arrayList = ArrayList<ArrayList<BookDetail>>()
        arrayList.add(presenter!!.loadTypeOne(document) as ArrayList<BookDetail>)
        arrayList.add(presenter!!.loadTypeTwo(document) as ArrayList<BookDetail>)
        arrayList.add(presenter!!.loadTypeThree(document) as ArrayList<BookDetail>)
        arrayList.add(presenter!!.loadTypeFour(document) as ArrayList<BookDetail>)
        arrayList.add(presenter!!.loadTypeFive(document) as ArrayList<BookDetail>)
        arrayList.add(presenter!!.loadTypeSix(document) as ArrayList<BookDetail>)
        arrayList.add(presenter!!.loadTypeSeven(document) as ArrayList<BookDetail>)
        arrayList.add(presenter!!.loadTypeEight(document) as ArrayList<BookDetail>)
        adapter!!.setNewData(arrayList)
    }
    private fun initData() {
        presenter!!.loadDocument(ConstantValues.BASE_URL)
    }


     fun loadPile(books :List<BookDetail>) {
        if(mPileLayout != null){
            if(mPileLayout!!.adapter == null){
                mPileLayout!!.setAdapter(object :PileLayout.Adapter(){
                    override fun getItemCount(): Int {
                        return if(books.isNotEmpty()){
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
                            var viewHolder: ViewHolder?= null
                            if(view!!.tag != null){
                                viewHolder = view!!.tag as ViewHolder
                            }else{
                                viewHolder =
                                    ViewHolder()
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

                        goBookDetail(books[position])
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
                itemAdapter.setOnItemClickListener { adapter,
                                                     view, position ->
                    goBookDetail(adapter.data[position] as BookDetail)
                }
            }

        }

    }



    inner class ItemAdapter:BaseQuickAdapter<BookDetail,BaseViewHolder>(R.layout.item_fragment_city_list){
        override fun convert(helper: BaseViewHolder, item: BookDetail) {
            helper.setText(R.id.name,item.bookName)
            GlideUtils.loadGif(context, helper.getView(R.id.image))
            if(item.bookCover == null){
                val exitBookDetail1 = presenter!!.getExitBookDetail(
                    item.bookName,
                    ConstantValues.BASE_URL,
                    item.bookUrl
                )
                if(exitBookDetail1.isEmpty()){
                    JsoupUtils.getFreeDocument(ConstantValues.BASE_URL+item.bookUrl ).subscribe(object :CustomBaseObserver<Document>(){
                        override fun next(o: Document?) {
                            val parseDocument = presenter!!.parseDocument(o!!)
                            GlideUtils.load(
                                context,
                                parseDocument.bookCover,
                                helper.getView(R.id.image)
                            )
                            item.bookCover = parseDocument.bookCover
                            item.bookIntro = parseDocument.bookIntro
                            parseDocument.bookUrl = item.bookUrl
                            parseDocument.isLooked = "false"
                            parseDocument.isBookCase = "false"
                            parseDocument.save()
                        }

                    })
                }else{
                    GlideUtils.load(
                        context,
                        exitBookDetail1[0].bookCover,
                        helper.getView(R.id.image)
                    )
                }

            }else{
                GlideUtils.load(
                    context,
                    item.bookCover ,
                    helper.getView(R.id.image)
                )
            }
        }
    }


    private class ViewWrapper(private val mTarget: View) {
        var width: Int
            get() = mTarget.layoutParams.width
            set(width) {
                mTarget.layoutParams.width = width
                mTarget.requestLayout()
            }

        var height: Int
            get() = mTarget.layoutParams.height
            set(height) {
                mTarget.layoutParams.width = height
                mTarget.requestLayout()
            }
    }

    override fun createPresenter(): BookCityPresenter {
        return BookCityPresenter()
    }



    private fun goBookDetail(bookDetail: BookDetail){
        val busMessage = BusMessage<Document>()
        busMessage.target = BookDetailActivity::class.java.simpleName
        busMessage.specialMessage = bookDetail.bookName
        busMessage.message = bookDetail.bookUrl
        EventBus.getDefault().postSticky(busMessage)
        startActivity(Intent(activity, BookDetailActivity::class.java))
    }
}