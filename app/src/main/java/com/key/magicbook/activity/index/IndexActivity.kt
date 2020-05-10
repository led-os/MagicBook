package com.key.magicbook.activity.index

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.key.magicbook.R
import com.key.magicbook.activity.index.booksecond.SecondFragment
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.bean.TabEntity
import kotlinx.android.synthetic.main.activity_index.*
import java.util.ArrayList

/**
 * created by key  on 2020/2/27
 */
class IndexActivity : MineBaseActivity<IndexPresenter>() {
    private val mFragments = ArrayList<Fragment>()
    private val mTitles = arrayOf("书架", "书角", "我的")
    private val tabEntities = ArrayList<CustomTabEntity>()
    //R.mipmap.index_book_video_blue,
    private val iconSelectIds = intArrayOf(
        R.mipmap.index_book_blue,
        R.mipmap.index_book_city_blue,
        R.mipmap.index_book_mine_blue
    )
    // R.mipmap.index_book_video_gray,
    private val iconUnSelectIds = intArrayOf(
        R.mipmap.index_book_gray,
        R.mipmap.index_book_city_gray,
        R.mipmap.index_book_mine_gray
    )


    //mFragments.add(BookVideoFragment())
    override fun initView() {
        if(mFragments.size == 0){
            mFragments.add(BookRackFragment.newInstance())
            mFragments.add(SecondFragment.newInstance())
            mFragments.add(MineFragment.newInstance(getUserInfo().userName))
        }

        for (i in this.iconSelectIds.indices) {
            this.tabEntities.add(
                TabEntity(
                    this.mTitles[i],
                    this.iconSelectIds[i],
                    this.iconUnSelectIds[i]
                )
            )
        }


        main_viewpager.offscreenPageLimit = 4
        main_viewpager.adapter = TabAdapter(mFragments,supportFragmentManager)

        tabLayout.setTabData(tabEntities)
        tabLayout.currentTab = 0
        main_viewpager.currentItem = 0

        setTabSelect()
        setViewPagerSelect()

    }



    private fun setViewPagerSelect() {
        main_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                tabLayout.currentTab = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
    private fun setTabSelect() {
        tabLayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                main_viewpager.currentItem = position
            }

            override fun onTabReselect(position: Int) {
            }
        })
    }
    override fun setLayoutId(): Int {
        return R.layout.activity_index
    }
    override fun createPresenter(): IndexPresenter? {
        return IndexPresenter()
    }
    inner class TabAdapter(fragmentList: List<Fragment>?, fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {

        private var mFragments: List<Fragment> = ArrayList()

        init {
            if (fragmentList != null) {
                mFragments = fragmentList
            }
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }
    }
}