package com.key.magicbook.set

import android.content.Intent
import com.key.magicbook.R
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.login.LoginActivity
import kotlinx.android.synthetic.main.activity_set.*

/**
 * created by key  on 2020/3/25
 */
class SetActivity : MineBaseActivity<SetPresenter>() {
    override fun createPresenter(): SetPresenter {
        return SetPresenter()
    }

    override fun initView() {
        setTitle(toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        quit.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
    override fun setLayoutId(): Int {
        return R.layout.activity_set
    }
}