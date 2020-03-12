package com.key.keylibrary.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar
import com.key.keylibrary.bean.BusMessage
import me.jessyan.autosize.internal.CustomAdapt
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * created by key  on 2019/10/10
 */
abstract class BaseFragment : Fragment(), CustomAdapt {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(setLayoutId(), container, false)
    }
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        registerEventBus(this)
        initView()
    }

    override fun isBaseOnWidth(): Boolean {
        return false
    }

    override fun getSizeInDp(): Float {
        return 640f
    }

    override fun onResume() {
        super.onResume()
        registerEventBus(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterEventBus(this)
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
    open fun receiveMessage(busMessage: BusMessage<Any>){

    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun onMessageReceive(busMessage: BusMessage<Any>) {
        if (busMessage.target == javaClass.simpleName) {
            receiveMessage(busMessage)
            removeEventBusMessage(busMessage)
        }
    }


    fun setTitle(view: View){
         ImmersionBar.with(activity!!).titleBar(view).init()
    }


    abstract fun setLayoutId(): Int
    abstract fun initView()
}