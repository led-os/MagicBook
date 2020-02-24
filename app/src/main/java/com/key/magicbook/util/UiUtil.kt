package com.key.magicbook.util

import android.os.Handler
import android.os.Process
import com.key.keylibrary.base.GlobalApplication
import com.key.magicbook.App

/**
 * created by key  on 2020/1/5
 */
class UiUtil {
    companion object {

        private fun getHandler(): Handler? {
            return  App.current()!!.getMineHandler()
        }
        private fun isRunOnUIThread(): Boolean {
            val myTid = Process.myTid()
            return myTid == getMainThreadId()
        }


        /**
         * 在主线程中运行
         *
         * @param r
         */
         fun runOnUIThread(r: Runnable) {
            if (isRunOnUIThread()) {
                r.run()
            } else {
                getHandler()!!.postDelayed(r, 0)
            }
        }


        private fun getMainThreadId(): Int {
            return App.current()!!.getMainThread()
        }
    }

}