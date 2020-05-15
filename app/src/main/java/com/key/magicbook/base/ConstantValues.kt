package com.key.magicbook.base

import com.key.keylibrary.base.GlobalApplication
import com.key.keylibrary.utils.UiUtils

/**
 * 项目中
 *   1.SharePreference中key的值
 *   2.网络请求接口
 * created by key  on 2019/2/1
 */
object ConstantValues {

    const val BASE_URL = "https://www.dingdiann.com/"

    val DOWNLOAD =
        GlobalApplication.getContext().getExternalFilesDir("Download")!!.absolutePath
    val FILE_PHOTO =
        UiUtils.getContext().getExternalFilesDir("TEMP")!!.absolutePath
    val FILE_BOOK =
        UiUtils.getContext().getExternalFilesDir("BOOK")!!.absolutePath
    val FILE_BOOK_CACHE =
        UiUtils.getContext().getExternalFilesDir("CACHE")!!.absolutePath
    const val TAKE_PHOTO = "take_photo_identification"
}
