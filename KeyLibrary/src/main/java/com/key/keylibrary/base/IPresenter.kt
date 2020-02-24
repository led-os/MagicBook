package com.key.keylibrary.base
/**
 * created by key  on 2019/1/31
 */
interface IPresenter<in V> {
    fun register(view: V)
    fun unRegister()
}
