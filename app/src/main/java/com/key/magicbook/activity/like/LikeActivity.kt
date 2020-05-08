package com.key.magicbook.activity.like

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.key.magicbook.R
import com.key.magicbook.activity.set.LikePresenter
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.db.BookLike
import com.key.magicbook.helper.WeSwipe
import com.key.magicbook.helper.WeSwipeHelper
import kotlinx.android.synthetic.main.activity_like.*
import kotlinx.android.synthetic.main.activity_set.*
import kotlinx.android.synthetic.main.activity_set.toolbar
import org.litepal.LitePal

/**
 * created by key  on 2020/3/25
 */
class LikeActivity : MineBaseActivity<LikePresenter>() {
    private var adapter:Adapter ?= null
    private var likes :ArrayList<BookLike> ?= null
    override fun createPresenter(): LikePresenter {
        return LikePresenter()
    }

    override fun initView() {
        setTitle(toolbar)
        initToolbar(toolbar)


        list.layoutManager = LinearLayoutManager(this)
        adapter = Adapter()
        list.adapter = adapter

        likes = ArrayList();
        val findAll = LitePal.findAll(BookLike::class.java)
        for(value in findAll){
            if(value.isLike == "true"){
                likes!!.add(value)
            }
        }

        adapter!!.setNewData(likes)

        WeSwipe.attach(list)
    }
    override fun setLayoutId(): Int {
        return R.layout.activity_like
    }


     class Adapter :BaseQuickAdapter<BookLike,LikeViewHolder>(R.layout.item_like){

        override fun convert(helper: LikeViewHolder, item: BookLike) {

        }
    }

     class LikeViewHolder(view: View) : BaseViewHolder(view), WeSwipeHelper.SwipeLayoutTypeCallBack{
        var slide: TextView? = null
        var contentView : View ?= null

         init {
             contentView = view.findViewById(R.id.content)
             slide = view.findViewById(R.id.slide)
         }
        override fun getSwipeWidth(): Float {
            return slide!!.width.toFloat()
        }

        override fun onScreenView(): View {
            return  contentView!!
        }

        override fun needSwipeLayout(): View {
            return contentView!!
        }

    }
}