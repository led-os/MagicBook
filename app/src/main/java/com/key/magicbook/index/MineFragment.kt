package com.key.magicbook.index

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import com.key.keylibrary.base.BaseFragment
import com.key.keylibrary.base.ShowImageActivity
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.util.BitmapUtil
import kotlinx.android.synthetic.main.fragment_index_mine.*

/**
 * created by key  on 2020/3/2
 */
class MineFragment :BaseFragment(){

    override fun setLayoutId(): Int {
        return R.layout.fragment_index_mine
    }

    override fun initView() {
        setIconBackground("")
        icon.setOnClickListener {
            val busMessage = BusMessage<Bitmap>()
            startActivity(Intent(activity,ShowImageActivity::class.java))
            activity!!.overridePendingTransition(0, 0)
            busMessage.target = ShowImageActivity::class.java.simpleName
            busMessage.data = icon.drawable.toBitmap()
            val mainActivity = activity as IndexActivity
            mainActivity.sendBusMessage(busMessage)
        }
    }


    @Suppress("DEPRECATION")
    private fun setIconBackground(url :String){
        val decodeResource = BitmapFactory.decodeResource(activity!!.resources, R.mipmap.test)
        val picturePixel = BitmapUtil.getPicturePixel(decodeResource)
        var tempColor = 0
        var tempCount = 0
        var sameCount = 0
        var finalColor = 0
        for(value in picturePixel){
            if(value != tempColor){
                tempColor = value
                if(sameCount > tempCount){
                    tempCount = sameCount
                    finalColor = value
                }
                sameCount = 0
            }else{
                sameCount++;
            }
        }

        val ints = intArrayOf(
            finalColor,
            activity!!.resources.getColor(R.color.dart_gray_color)
        )
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, ints)
        icon_root.setBackgroundDrawable(gradientDrawable)
        val margin = UiUtils.getScreenHeight(activity) / 10
        val layoutParams = icon.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = margin
        layoutParams.bottomMargin = margin
        icon.layoutParams = layoutParams
    }

}