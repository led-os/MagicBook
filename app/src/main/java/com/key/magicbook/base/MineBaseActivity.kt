package com.key.magicbook.base

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import com.key.keylibrary.base.BaseActivity
import com.key.keylibrary.base.BasePresenter
import com.key.keylibrary.base.IView
import com.key.magicbook.bean.UserInfo
import com.umeng.analytics.MobclickAgent
import org.litepal.LitePal

/**
 * created by key  on 2020/1/5
 */
abstract class MineBaseActivity<P : BasePresenter<Activity>> : BaseActivity(),IView{
    protected var presenter :P ?=null


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

    abstract fun createPresenter(): P?


    fun initToolbar(toolbar : Toolbar){
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }



    fun getUserInfo():UserInfo{
        val findAll = LitePal.findAll(UserInfo::class.java)
        for(value in findAll){
            if(value.isLogin == "true"){
                return value
            }
        }
        return UserInfo()
    }



    fun isLogin() :Boolean{
        return getUserInfo().isLogin == "true"
    }
}