package com.key.magicbook.base

import android.app.Activity
import androidx.fragment.app.Fragment
import com.key.keylibrary.base.BaseFragment
import com.key.keylibrary.base.BasePresenter
import com.key.keylibrary.base.IView

/**
 * created by key  on 2020/5/10
 */
abstract class MineBaseFragment<P : BasePresenter<Fragment>>  : BaseFragment(),IView {
    protected var presenter :P ?=null

    override fun bindView() {
        if(presenter == null){
            presenter = createPresenter()
            presenter!!.register(this)
        }
    }
    override fun unBindView() {
        if(presenter != null){
            presenter!!.unRegister()
        }
    }


    abstract fun createPresenter() :P
    override fun onDestroy() {
        super.onDestroy()
        unBindView()
    }
}