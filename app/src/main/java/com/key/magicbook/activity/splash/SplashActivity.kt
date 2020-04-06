package com.key.magicbook.activity.splash
import android.content.Intent
import android.os.Handler
import com.key.magicbook.R
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.activity.index.IndexActivity

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
