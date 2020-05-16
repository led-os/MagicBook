package com.key.magicbook.activity.options

import android.app.Activity
import android.graphics.Typeface
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.key.keylibrary.base.BasePresenter
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.activity.read.SettingDialogFragment
import com.key.magicbook.base.MineBaseActivity
import kotlinx.android.synthetic.main.dialog_setting.*
import org.greenrobot.eventbus.EventBus

/**
 * 用于选择时间以及传入的一些相关的选项 采用开源库 OptionsPickerView
 * 详细使用地址请参考
 *      https://github.com/Bigkoo/Android-PickerView
 *      https://www.jianshu.com/p/41aa6c283715
 * created by key  on 2019/5/20
 */
@Suppress("UNCHECKED_CAST")
class OptionsPickViewActivity : MineBaseActivity<BasePresenter<Activity>>() {
    override fun createPresenter(): BasePresenter<Activity>? {
        return OptionPickViewPresenter()
    }

    override fun initView() {


    }

    override fun setLayoutId(): Int {
        return R.layout.activity_option_pick
    }

    override fun receiveMessage(busMessage: BusMessage<Any>) {
        super.receiveMessage(busMessage)
        if(busMessage.message == "typeface"){
            val list = busMessage.data as List<Typeface>
            runOnUiThread { loadOptionsForTypeface(list,busMessage.specialMessage.toInt()) }
        }else if(busMessage.message == "pageMode"){
            val list = busMessage.data as List<String>
            runOnUiThread { loadPageMode(list,busMessage.specialMessage.toInt()) }
        }
    }



    private fun loadPageMode(list :List<String>, beChoosePosition: Int){
        val pvOptions = OptionsPickerBuilder(this) { options1, _, _, _ ->
            val busMessage = BusMessage<Int>()
            busMessage.target = SettingDialogFragment::class.java.simpleName
            busMessage.data = options1
            busMessage.message = "pageMode"
            sendBusMessage(busMessage)
            finish()
        }
            .setTitleBgColor(resources.getColor(R.color.read_bg))
            .setBgColor(resources.getColor(R.color.white))
            .setTitleColor(resources.getColor(R.color.white))
            .setCancelColor(resources.getColor(R.color.white))
            .setSubmitColor(resources.getColor(R.color.white))
            .build<Any>()
        pvOptions.setPicker(list as List<Any>?)
        pvOptions.setTitleText("翻页模式")
        pvOptions.setSelectOptions(beChoosePosition)
        pvOptions.setKeyBackCancelable(false)
        pvOptions.setOnDismissListener { finish() }
        pvOptions.show()
    }
    private fun loadOptionsForTypeface(list :List<Typeface>, beChoosePosition: Int){
        val options1Items = arrayListOf<String>(
            "默认字体","旗黑字体","卡通字体","宋体")
        val pvOptions = OptionsPickerBuilder(this) { options1, _, _, _ ->
            val busMessage = BusMessage<Int>()
            busMessage.target = SettingDialogFragment::class.java.simpleName
            busMessage.data = options1
            busMessage.message = "typeface"
            sendBusMessage(busMessage)
            finish()
        }
            .setTitleBgColor(resources.getColor(R.color.read_bg))
            .setBgColor(resources.getColor(R.color.white))
            .setTitleColor(resources.getColor(R.color.white))
            .setCancelColor(resources.getColor(R.color.white))
            .setSubmitColor(resources.getColor(R.color.white))
            .build<Any>()
        pvOptions.setPicker(options1Items as List<Any>?)
        pvOptions.setTitleText("选择字体")
        pvOptions.setSelectOptions(beChoosePosition)
        pvOptions.setKeyBackCancelable(false)
        pvOptions.setOnDismissListener { finish() }
        pvOptions.show()
    }
}
