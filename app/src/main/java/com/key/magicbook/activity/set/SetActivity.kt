package com.key.magicbook.activity.set

import android.content.ContentValues
import android.content.Intent
import com.key.magicbook.R
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.activity.login.LoginActivity
import com.key.magicbook.bean.UserInfo
import kotlinx.android.synthetic.main.activity_set.*
import org.litepal.LitePal

/**
 * created by key  on 2020/3/25
 */
class SetActivity : MineBaseActivity<SetPresenter>() {
    override fun createPresenter(): SetPresenter {
        return SetPresenter()
    }

    override fun initView() {
        setTitle(toolbar)
        initToolbar(toolbar)
        quit.setOnClickListener {
            val contentValues = ContentValues()
            contentValues.put("isLogin","false")
            LitePal.updateAll(UserInfo::class.java,contentValues)
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(0,0)
        }
    }
    override fun setLayoutId(): Int {
        return R.layout.activity_set
    }
}