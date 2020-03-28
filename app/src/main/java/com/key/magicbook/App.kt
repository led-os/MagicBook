package com.key.magicbook

import android.os.Handler
import com.key.keylibrary.base.GlobalApplication
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

/**
 * created by key  on 2020/1/5
 */
class App : GlobalApplication() {

    override fun onCreate() {
        super.onCreate()
        app = this
        initUmeng()
    }


    private fun initUmeng(){
        UMConfigure.init(this, "5e7c2ff0167edd72da0000a8", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        UMConfigure.setLogEnabled(true);
    }
    fun getMineHandler():Handler{
        return getHandler()
    }
    fun getMainThread():Int{
        return getMainThreadId()
    }
    companion object {

        private var app: App? = null

        fun current(): App? {
            return app
        }
    }
}