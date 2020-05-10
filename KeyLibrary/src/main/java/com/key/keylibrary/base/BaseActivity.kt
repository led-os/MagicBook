package com.key.keylibrary.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import com.gyf.immersionbar.ImmersionBar
import com.key.keylibrary.R
import com.key.keylibrary.bean.BusMessage
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeCompat
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.internal.CustomAdapt
import me.jessyan.autosize.onAdaptListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * created by key  on 2019/10/7
 */
abstract class BaseActivity : AppCompatActivity(),CustomAdapt {
    private var unBinder: Unbinder? = null
    open var handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView()
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            fixOrientation()
        }
        initSystemBar()
        unBinder = ButterKnife.bind(this)
        setContentView(setLayoutId())
        handler.postDelayed({
            registerEventBus(this)
        },100)
        initView()
        initAuto()

    }



    abstract fun bindView()
    /**
     * fix error Only fullscreen opaque activities can request orientation
     * @return
     */
     open fun fixOrientation(): Boolean {
        try {
            val field =
                Activity::class.java.getDeclaredField("mActivityInfo")
            field.isAccessible = true
            val o =
                field[this] as ActivityInfo
            o.screenOrientation = -1
            field.isAccessible = false
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }


    /**
     * fix error Only fullscreen opaque activities can request orientation
     * @return
     */
    @SuppressLint("PrivateApi")
    open fun isTranslucentOrFloating(): Boolean {
        var isTranslucentOrFloating = false
        try {
            val styleableRes =
                Class.forName("com.android.internal.R\$styleable").getField("Window")[null] as IntArray
            val ta = obtainStyledAttributes(styleableRes)
            val m = ActivityInfo::class.java.getMethod(
                "isTranslucentOrFloating",
                TypedArray::class.java
            )
            m.isAccessible = true
            isTranslucentOrFloating = m.invoke(null, ta) as Boolean
            m.isAccessible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isTranslucentOrFloating
    }
    override fun onResume() {
        super.onResume()
        registerEventBus(this)
    }

    override fun onDestroy() {
        unregisterEventBus(this)
        unBinder!!.unbind()
        super.onDestroy()
    }


    /**
     * 初始化布局
     */
    abstract fun initView()


    private fun initAuto() {
        AutoSize.initCompatMultiProcess(this)
        AutoSizeConfig.getInstance()
                .setCustomFragment(true).onAdaptListener = object : onAdaptListener {
            override fun onAdaptBefore(target: Any, activity: Activity) {}

            override fun onAdaptAfter(target: Any, activity: Activity) {}
        }
    }


    override fun getResources(): Resources {
        AutoSizeCompat.autoConvertDensity(super.getResources(), 384f, true)
        return super.getResources()
    }


    override fun onPause() {
        if(isEventBusRegister(this)){
           unregisterEventBus(this)
        }
        super.onPause()
    }

    private fun isEventBusRegister(subscribe: Any): Boolean {
        return EventBus.getDefault().isRegistered(subscribe)
    }

    private fun registerEventBus(subscribe: Any) {
        if (!isEventBusRegister(subscribe)) {
            EventBus.getDefault().register(subscribe)
        }
    }

    private fun unregisterEventBus(subscribe: Any) {
        if (isEventBusRegister(subscribe)) {
            EventBus.getDefault().unregister(subscribe)
        }
    }

    private fun removeEventBusMessage(message: Any) {
        EventBus.getDefault().removeStickyEvent(message)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun onMessageReceive(busMessage: BusMessage<Any>) {
        if(busMessage.target != null){
            if (busMessage.target == javaClass.simpleName) {
                handler.post {
                    receiveMessage(busMessage)
                    removeEventBusMessage(busMessage)
                }
            }
        }else{
            handler.post {
                val receiveAllMessage = receiveAllMessage(busMessage)
                if(receiveAllMessage){
                    removeEventBusMessage(busMessage)
                }

            }
        }
    }

    open fun receiveMessage(busMessage: BusMessage<Any>){

    }
    open fun receiveAllMessage(busMessage: BusMessage<Any>) :Boolean{
        return false
    }
    abstract fun setLayoutId(): Int

    fun <T> sendBusMessage(busMessage: BusMessage<T>){
        EventBus.getDefault().postSticky(busMessage)
    }
    protected open fun initSystemBar() {
        ImmersionBar.with(this).navigationBarColor(R.color.white)
                .statusBarDarkFont(true, 0.7f)
                .fitsSystemWindows(fitsSystemWindows()).init()
    }
    protected open fun fitsSystemWindows(): Boolean {
        return false
    }
    protected open fun setTitle(view :View){
        ImmersionBar.with(this).titleBar(view).init()
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(0,0)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,0)
    }


    override fun isBaseOnWidth(): Boolean {
        return true
    }

    override fun getSizeInDp(): Float {
        return 384f
    }

    open fun hintKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
