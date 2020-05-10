package com.key.keylibrary.activity

import android.view.WindowManager
import com.key.keylibrary.R
import com.key.keylibrary.base.BaseActivity
import com.key.keylibrary.bean.BusMessage
import me.jessyan.autosize.internal.CustomAdapt

class MainActivity : BaseActivity(), CustomAdapt {

    override fun setLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun bindView() {
        TODO("Not yet implemented")
    }

    override fun initView() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun isBaseOnWidth(): Boolean {
        return false
    }

    override fun getSizeInDp(): Float {
        return 640f
    }
}
