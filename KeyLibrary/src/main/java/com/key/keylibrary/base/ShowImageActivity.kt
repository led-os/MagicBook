package com.key.keylibrary.base

import android.graphics.Bitmap
import com.key.keylibrary.R
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.tamsiree.rxui.view.scaleimage.ImageSource
import kotlinx.android.synthetic.main.activity_show_image.*

/**
 * created by key  on 2020/3/12
 */
class ShowImageActivity : BaseActivity() {
    override fun initView() {
        val layoutParams = scale_image.layoutParams
        layoutParams.height = UiUtils.getScreenHeight(this) /2
        layoutParams.width = UiUtils.getScreenWidth(this)
        scale_image.layoutParams = layoutParams
        close.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun receiveMessage(busMessage: BusMessage<Any>) {
        val bitmap = busMessage.data as Bitmap
        runOnUiThread {
            if(scale_image != null){
                scale_image.setImage(ImageSource.bitmap(bitmap))
            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_show_image
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}