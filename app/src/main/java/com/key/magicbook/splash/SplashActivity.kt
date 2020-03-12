package com.key.magicbook.splash
import android.content.Intent
import android.os.Handler
import com.key.keylibrary.bean.BusMessage
import com.key.magicbook.R
import com.key.magicbook.base.BaseContract
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.index.IndexActivity
import com.key.magicbook.widget.JumpOnTextView
import kotlinx.android.synthetic.main.activity_main.*

class SplashActivity : MineBaseActivity<SplashPresenter>() {
    override fun initView() {
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, IndexActivity::class.java)
            startActivity(intent)
            finish()
        },100)
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun createPresenter(): SplashPresenter? {
        return SplashPresenter()
    }
}
