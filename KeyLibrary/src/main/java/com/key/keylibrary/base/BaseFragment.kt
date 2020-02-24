package com.key.keylibrary.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.key.keylibrary.bean.BusMessage
import me.jessyan.autosize.internal.CustomAdapt
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * created by key  on 2019/10/10
 */
abstract class BaseFragment : Fragment(), CustomAdapt {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        registerEventBus(this)
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
    abstract fun receiveMessage(busMessage: BusMessage<Any>)

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun onMessageReceive(busMessage: BusMessage<Any>) {
        if (busMessage.target == javaClass.simpleName) {
            receiveMessage(busMessage)
            removeEventBusMessage(busMessage)
        }
    }
}