package com.key.keylibrary.base
import java.lang.ref.WeakReference

/**
 * created by key  on 2019/1/31
 */
open class BasePresenter<V> : IPresenter<V> {
    /**
     *弱引用
     */
    var iView: WeakReference<in V>? = null
    override fun register(view: V) {
        iView = WeakReference(view)
    }
    /**
     *  view 表示的是当前被主持的对象 可以是Activity或者是Fragment
     */
    override fun unRegister() {
        iView?.clear()
    }
}
