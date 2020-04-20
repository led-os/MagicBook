package com.key.magicbook.base

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.key.keylibrary.base.BaseActivity
import com.key.keylibrary.base.BasePresenter
import com.key.keylibrary.base.IView
import com.key.magicbook.R
import com.tamsiree.rxui.view.progressing.style.CubeGrid
import com.umeng.analytics.MobclickAgent

/**
 * created by key  on 2020/1/5
 */
abstract class MineBaseActivity<P : BasePresenter<Activity>> : BaseActivity(),IView{
    private var presenter :P ?=null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        bindView()
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        unBindView()
    }
    override fun bindView() {
        if(presenter != null){
            presenter = createPresenter()
            presenter!!.register(this)
        }
    }

    override fun unBindView() {
        if(presenter != null){
            presenter!!.unRegister()
        }
    }

    abstract fun createPresenter(): P?


    fun initToolbar(toolbar : Toolbar){
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }



}