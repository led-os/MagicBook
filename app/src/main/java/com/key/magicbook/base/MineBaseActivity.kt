package com.key.magicbook.base

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import com.key.keylibrary.base.BaseActivity
import com.key.keylibrary.base.BasePresenter
import com.key.keylibrary.base.IView
import com.key.keylibrary.bean.BusMessage

/**
 * created by key  on 2020/1/5
 */
abstract class MineBaseActivity<P : BasePresenter<Activity>> : BaseActivity(),IView{
    private var presenter :P ?=null
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        bindView()
    }


    override fun onDestroy() {
        super.onDestroy()
        unBindView()
    }
    override fun bindView() {
       presenter = createPresenter()
       presenter!!.register(this)
    }

    override fun unBindView() {
        if(presenter != null){
            presenter!!.unRegister()
        }
    }



    abstract fun createPresenter(): P?

}