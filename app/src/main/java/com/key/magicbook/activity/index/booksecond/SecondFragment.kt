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

    var pageTitles = arrayOf(
        "首页","玄幻奇幻", "武侠仙侠",
        "都市言情", "历史军事",
        "科幻灵异","网游竞技","女生频道")

    var pageUrls = arrayOf(
        "首页","https://www.dingdiann.com/ddk_1/",
        "https://www.dingdiann.com/ddk_2/",
        "https://www.dingdiann.com/ddk_3/",
        "https://www.dingdiann.com/ddk_4/",
        "https://www.dingdiann.com/ddk_5/",
        "https://www.dingdiann.com/ddk_6/",
        "https://www.dingdiann.com/ddk_7/")
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
        for((index ,value) in pageTitles.withIndex()){
            if(value == "首页"){
                pageFragments.add(BookCityFragment.newInstance())
            }else{
               pageFragments.add(BookTypeFragment.newInstance(pageUrls[index]))
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
            return pageTitles.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return pageTitles[position]
        }
    }

    override fun createPresenter(): BookSecondPresenter {
        return BookSecondPresenter()
    }
}