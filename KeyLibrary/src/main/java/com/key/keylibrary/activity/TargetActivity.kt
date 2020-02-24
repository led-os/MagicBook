package com.key.keylibrary.activity

import android.content.Intent
import com.key.keylibrary.R
import com.key.keylibrary.base.BaseActivity
import com.key.keylibrary.base.BaseFragment
import com.key.keylibrary.bean.BusMessage
import kotlinx.android.synthetic.main.activity_target.*
import me.jessyan.autosize.internal.CustomAdapt
import java.lang.StringBuilder

/**
 * created by key  on 2019/11/18
 */
class TargetActivity : BaseActivity(), CustomAdapt {

    override fun setLayoutId(): Int {
        return R.layout.activity_target
    }

    override fun initView() {
        val busMessage = BusMessage<String>()
        val target = MainActivity::class
        val intent = Intent(this@TargetActivity, target.java)
        some_message.setOnClickListener {
            busMessage.data = "MainActivity"
            busMessage.target = target.simpleName
            sendBusMessage(busMessage as BusMessage<Any>)
            startActivity(intent)
        }
    }

    override fun receiveMessage(busMessage: BusMessage<Any>) {
        val message = busMessage as BusMessage<String>
        some_message.text = message.data
    }
    override fun isBaseOnWidth(): Boolean {
        return false
    }

    override fun getSizeInDp(): Float {
        return 640f
    }
}
