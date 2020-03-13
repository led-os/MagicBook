package com.key.magicbook.index

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.*
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLSpanElement
import com.key.keylibrary.base.BaseFragment
import com.key.keylibrary.bean.BusMessage
import com.key.magicbook.R
import kotlinx.android.synthetic.main.fragment_index_mine_book.*


/**
 * created by key  on 2020/3/2
 */
class BookRackFragment :BaseFragment(),SensorEventListener{
    private var defaultSensor :Sensor ?= null
    private var systemService :SensorManager ?= null


    override fun setLayoutId(): Int {
        return  R.layout.fragment_index_mine_book
    }

    override fun initView() {
        setTitle(refresh)
        refresh.setEnableRefresh(false)
        refresh.setEnableLoadMore(false)


        val layoutManager = GridLayoutManager(activity, 3)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        list.layoutManager = layoutManager


        val mineBookAdapter = MineBookAdapter(R.layout.item_book_mine)
        list.adapter = mineBookAdapter

        val arrayList = ArrayList<String>()

        mineBookAdapter.setNewData(arrayList)


        systemService = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        defaultSensor = systemService!!.getDefaultSensor(Sensor.TYPE_LIGHT)

    }


    override fun onResume() {
        super.onResume()
        if(systemService != null && defaultSensor != null){
            systemService!!.registerListener(this,defaultSensor!!,SensorManager.SENSOR_DELAY_GAME)
        }
    }


    override fun onStop() {
        super.onStop()
        if(systemService != null){
            systemService!!.unregisterListener(this)
        }
    }

    inner class MineBookAdapter(res :Int) :BaseQuickAdapter<String,BaseViewHolder>(res){
        override fun convert(helper: BaseViewHolder, item: String) {

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        val values = event!!.values
        if(event.sensor.type == Sensor.TYPE_LIGHT){

        }
    }


}