package com.key.magicbook

import android.os.Handler
import com.key.keylibrary.base.GlobalApplication

/**
 * created by key  on 2020/1/5
 */
class App : GlobalApplication() {

    override fun onCreate() {
        super.onCreate()
        app = this
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