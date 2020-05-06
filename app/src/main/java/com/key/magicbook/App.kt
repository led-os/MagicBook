package com.key.magicbook

import android.os.Handler
import com.allen.library.RxHttpUtils
import com.allen.library.config.OkHttpConfig
import com.allen.library.cookie.store.SPCookieStore
import com.allen.library.interceptor.HeaderInterceptor
import com.allen.library.utils.AppUtils
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
        initRxHttpUtil();
        AppUtils.handleSSLHandshake()
    }


    private fun initRxHttpUtil(){
       val okHttpClient = OkHttpConfig.Builder(this)
            //全局的请求头信息
            .setHeaders {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap
            }
            .setCache(true)
            .setHasNetCacheTime(10)
            .setNoNetCacheTime(3600)
            .setReadTimeout(30)
            .setWriteTimeout(30)
            .setConnectTimeout(30)
            .setDebug(true)
            .build();


        RxHttpUtils
            .getInstance()
            .init(this)
            .config()
            .setBaseUrl("https://www.qidian.com/")
            .setOkClient(okHttpClient);

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