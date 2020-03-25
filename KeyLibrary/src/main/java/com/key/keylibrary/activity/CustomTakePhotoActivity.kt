package com.key.keylibrary.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.app.TakePhotoImpl
import com.jph.takephoto.model.CropOptions
import com.jph.takephoto.model.InvokeParam
import com.jph.takephoto.model.TContextWrap
import com.jph.takephoto.model.TResult
import com.jph.takephoto.permission.InvokeListener
import com.jph.takephoto.permission.PermissionManager
import com.jph.takephoto.permission.TakePhotoInvocationHandler
import com.key.keylibrary.base.ConstantValues
import com.key.keylibrary.bean.BusMessage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileNotFoundException


/**
 * 获取图片界面用于修改头像
 * created by key  on 2019/3/18
 */
class CustomTakePhotoActivity : AppCompatActivity(), TakePhoto.TakeResultListener, InvokeListener {
    private var takePhoto: TakePhoto? = null
    private var invokeParam: InvokeParam? = null
    private var file :File ?= null
    private var fileName :String = ""
    object Tag{
        const val TagSingle = 1
        const val TagMultiple = 2
        const val TagCamera = 3
        const val TagAlbum = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
              fixOrientation()
        }
        getTakePhoto()!!.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getTakePhoto()!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        getTakePhoto()!!.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this)
    }

    override fun invoke(invokeParam: InvokeParam): PermissionManager.TPermissionType {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.method)
        if (PermissionManager.TPermissionType.WAIT == type) {
            this.invokeParam = invokeParam
        }
        return type
    }

    private fun getTakePhoto(): TakePhoto? {
        if (takePhoto == null) {
            takePhoto = TakePhotoInvocationHandler.of(this).bind(TakePhotoImpl(this, this)) as TakePhoto
        }
        return takePhoto
    }

    override fun takeSuccess(result: TResult) {
        if (result.image != null) {
            if (result.image.originalPath.isNotEmpty()) {
                saveImageToGallery(this,file!!,fileName)
                val originalPath = result.image.originalPath
                val busMessage = BusMessage<String>()
                busMessage.message = ConstantValues.TAKE_PHOTO
                busMessage.data = originalPath
                EventBus.getDefault().postSticky(busMessage)
                finish()
            }
        }

    }


    override fun takeFail(result: TResult, msg: String) {
        Log.e("img","takeFail")
        finish()
    }

    override fun takeCancel() {
        finish()
    }


    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            return
        }
        super.setRequestedOrientation(requestedOrientation)
    }

    private fun isTranslucentOrFloating(): Boolean {
        var isTranslucentOrFloating = false
        try {
            val styleableRes = Class.forName("com.android.internal.R\$styleable").getField("Window")[null] as IntArray
            val ta = obtainStyledAttributes(styleableRes)
            val m = ActivityInfo::class.java.getMethod("isTranslucentOrFloating", TypedArray::class.java)
            m.isAccessible = true
            isTranslucentOrFloating = m.invoke(null, ta) as Boolean
            m.isAccessible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isTranslucentOrFloating
    }


    private fun fixOrientation(): Boolean {
        try {
            val field = Activity::class.java.getDeclaredField("mActivityInfo")
            field.isAccessible = true
            val o = field[this] as ActivityInfo
            o.screenOrientation = -1
            field.isAccessible = false
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }


    private fun saveImageToGallery(context: Context, file:File, fileName :String) {
        try {
            MediaStore.Images.Media.insertImage(context.contentResolver,
                    file.absolutePath, fileName, null)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val paths = arrayOf<String>(file.absolutePath)
            MediaScannerConnection.scanFile(context, paths, null, null)
        } else {
            val intent: Intent
            if (file.isDirectory) {
                intent = Intent(Intent.ACTION_MEDIA_MOUNTED)
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver")
                intent.data = Uri.fromFile(Environment.getExternalStorageDirectory())
            } else {
                intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                intent.data = Uri.fromFile(file)
            }
            context.sendBroadcast(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }


    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }


    private fun takePhoto(busMessage: BusMessage<Any>){
        fileName  = System.currentTimeMillis().toString() + ".jpg"
        file = File(ConstantValues.FILE_PHOTO, fileName)
        if (!file!!.parentFile!!.exists()) {
            file!!.parentFile!!.mkdirs()
        }
        val uri = Uri.fromFile(file)
        val size = Math.min(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        val cropOptions = CropOptions.Builder().setOutputX(size).setOutputX(size).setWithOwnCrop(false).create()

        val way =  busMessage.message.toInt()
        when(busMessage.tag){
            Tag.TagSingle ->{
                when(way){
                    Tag.TagCamera->{
                        takePhoto!!.onPickFromCaptureWithCrop(uri, cropOptions)
                    }
                    Tag.TagAlbum->{
                        takePhoto!!.onPickFromGalleryWithCrop(uri, cropOptions)
                    }
                }
            }
            Tag.TagMultiple ->{
                takePhoto!!.onPickMultipleWithCrop(9, cropOptions)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun onMessageReceive(busMessage: BusMessage<Any>) {
        if (busMessage.target == javaClass.simpleName) {
            runOnUiThread {
                takePhoto(busMessage)
            }
            EventBus.getDefault().removeStickyEvent(busMessage)
        }
    }
}
