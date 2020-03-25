package com.key.keylibrary.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import java.math.BigDecimal

/**
 * created by key  on 2019/10/11
 */
object FontUtil {

//    val displayMetrics: DisplayMetrics
//        get() {
//            val windowManager = UiUtils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
//            val displayMetrics = DisplayMetrics()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                windowManager.defaultDisplay.getRealMetrics(displayMetrics)
//            } else {
//                windowManager.defaultDisplay.getMetrics(displayMetrics)
//            }
//            return displayMetrics
//        }
//
//    fun changeViewSize(viewGroup: ViewGroup, screenWidth: Int, screenHeight: Int) {//传入Activity顶层Layout,屏幕宽,屏幕高
//        var adjustFontSize = adjustFontSize(screenWidth, screenHeight)
//        if (screenHeight > 1920) {
//            adjustFontSize = 1f
//        }
//        for (i in 0 until viewGroup.childCount) {
//            val v = viewGroup.getChildAt(i)
//            if (v is ViewGroup) {
//                changeViewSize(v, screenWidth, screenHeight)
//            } else if (v is Button) {//按钮加大这个一定要放在TextView上面，因为Button也继承了TextView
//                v.textSize = adjustFontSize * (v.textSize / displayMetrics.density)
//            } else if (v is TextView) {
//                v.textSize = adjustFontSize * (v.textSize / displayMetrics.density)
//            }
//        }
//    }
//
//
//    //获取字体大小
//    fun adjustFontSize(screenWidth: Int, screenHeight: Int): Float {
//        val bigDecimal = BigDecimal(2340)
//        val bigDecimal2 = BigDecimal(screenHeight)
//        var rate = bigDecimal.divide(bigDecimal2, BigDecimal.ROUND_DOWN)
//        var add = BigDecimal(0)
//        if (screenHeight < 2340) {
//            add = rate.divide(BigDecimal(100)).multiply(BigDecimal(40))
//        }
//        rate = rate.add(add)
//        Log.e("RATE", "rate :" + rate + "height :" + screenHeight + "width :" + screenWidth)
//        return rate.toFloat()
//    }
}
