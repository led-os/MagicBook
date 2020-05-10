package com.key.keylibrary.base

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import com.key.keylibrary.R
import com.key.keylibrary.activity.CustomTakePhotoActivity
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.tamsiree.rxui.view.scaleimage.ImageSource
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_show_image.*
import org.greenrobot.eventbus.EventBus

/**
 * created by key  on 2020/3/12
 */
class ShowImageActivity : BaseActivity() {
    override fun bindView() {

    }

    override fun initView() {
        val layoutParams = scale_image.layoutParams
        layoutParams.height = UiUtils.getScreenHeight(this) /2
        layoutParams.width = UiUtils.getScreenWidth(this)
        scale_image.layoutParams = layoutParams
        close.setOnClickListener {
            finish()
        }

        cancel.setOnClickListener {
            finish()
        }
        camera.setOnClickListener {
            takePhoto( CustomTakePhotoActivity.Tag.TagCamera.toString())
        }
        album.setOnClickListener {
            takePhoto(  CustomTakePhotoActivity.Tag.TagAlbum.toString())
            finish()
        }
    }

    override fun receiveMessage(busMessage: BusMessage<Any>) {
        val bitmap = busMessage.data as Bitmap
        if(scale_image != null){
            scale_image.setImage(ImageSource.bitmap(bitmap))
            when(busMessage.tag){
                0->{
                    close.visibility = View.INVISIBLE
                }
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

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun takePhoto(message :String){
        val busMessage = BusMessage<String>()
        busMessage.tag = CustomTakePhotoActivity.Tag.TagSingle
        busMessage.message = message
        busMessage.target = CustomTakePhotoActivity::class.java.simpleName
        sendBusMessage(busMessage)
        startActivity(Intent(this@ShowImageActivity,CustomTakePhotoActivity::class.java))
        finish()
        overridePendingTransition(0, 0)
    }
}