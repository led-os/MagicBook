package com.key.magicbook.login

import com.key.magicbook.R
import com.key.magicbook.base.MineBaseActivity

/**
 * created by key  on 2020/3/13
 */
class LoginActivity :MineBaseActivity<LoginPresenter>() {
    override fun createPresenter(): LoginPresenter? {
        return LoginPresenter()
    }

    override fun initView() {

    }

    override fun setLayoutId(): Int {
          return R.layout.activity_login
    }
}