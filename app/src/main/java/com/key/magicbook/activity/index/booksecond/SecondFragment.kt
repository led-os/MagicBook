package com.key.magicbook.activity.index.booksecond

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.key.keylibrary.base.BaseFragment
import com.key.magicbook.R
import com.key.magicbook.activity.index.bookcity.BookCityFragment
import com.key.magicbook.activity.index.booktype.BookTypeFragment
import com.key.magicbook.activity.index.booktype.BookTypePresenter
import com.key.magicbook.base.MineBaseFragment
import kotlinx.android.synthetic.main.fragment_second.*

/**
 * created by key  on 2020/4/30
 */
class SecondFragment : MineBaseFragment<BookSecondPresenter>() {
    var pageFragments :ArrayList<BaseFragment> = ArrayList();
    companion object{
        fun newInstance(): SecondFragment {
            val secondFragment =
                SecondFragment()
            val bundle = Bundle()
            secondFragment.arguments = bundle
            return secondFragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments
    }



    override fun setLayoutId(): Int {
        return R.layout.fragment_second
    }

    override fun initView() {
        setTitle(indicator)
        for((index ,value) in presenter!!.getBookUrlsName().withIndex()){
            if(value == "首页"){
                pageFragments.add(BookCityFragment.newInstance())
            }else{
                pageFragments.add(BookTypeFragment.newInstance(presenter!!.getBookUrls()[index]))
            }

        }
        viewpager.setCanScroll(true)
        viewpager.offscreenPageLimit = 8
        viewpager.adapter = VpAdapter(activity!!.supportFragmentManager)
        indicator
            .setExpand(true)
            .setViewPager(viewpager)
    }
    inner class VpAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
          return  pageFragments[position]
        }

        override fun getCount(): Int {
            return presenter!!.getBookUrlsName().size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return  presenter!!.getBookUrlsName()[position]
        }
    }

    override fun createPresenter(): BookSecondPresenter {
        return BookSecondPresenter()
    }
}