package com.key.magicbook.splash
import android.os.Handler
import com.key.keylibrary.bean.BusMessage
import com.key.magicbook.R
import com.key.magicbook.base.BaseContract
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.widget.JumpOnTextView
import kotlinx.android.synthetic.main.activity_main.*

class SplashActivity : MineBaseActivity<SplashPresenter>(),BaseContract.View {
    override fun initView() {
        Handler().postDelayed({ splash.mineLoop() },1000)
        splash.onAnimationEndListener = object : JumpOnTextView.OnAnimationEndListener{
            override fun animationEnd() {

            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun createPresenter(): SplashPresenter? {
        return SplashPresenter()
    }

    override fun receiveMessage(busMessage: BusMessage<Any>) {

    }

    override fun onView(resultCode: Int, requestCode: Int, result: Any) {


    }
}
