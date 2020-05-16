package com.key.magicbook.activity.aboutus

import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.key.keylibrary.widget.CustomWebView
import com.key.magicbook.R
import com.key.magicbook.activity.set.AboutUsPresenter
import com.key.magicbook.base.MineBaseActivity
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.activity_about_us.toolbar

/**
 * created by key  on 2020/3/25
 */
class AboutUsActivity : MineBaseActivity<AboutUsPresenter>() {
    override fun createPresenter(): AboutUsPresenter {
        return AboutUsPresenter()
    }

    override fun initView() {
        setTitle(toolbar)
        initToolbar(toolbar)
        val web = CustomWebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.javaScriptCanOpenWindowsAutomatically = true;
        web.settings.setSupportZoom(true)
        web.settings.useWideViewPort = true;
        web.settings.loadWithOverviewMode = true
        val url = "https://www.dingdiann.com"
        AgentWeb.with(this)
                .setAgentWebParent(web_root, LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebView(web)
                .createAgentWeb()
                .ready()
                .go(url)
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_about_us
    }

}