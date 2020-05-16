package com.key.magicbook.db

import org.litepal.crud.LitePalSupport

/**
 * created by key  on 2020/5/16
 */
class BookRank  : LitePalSupport() {
    var isLoad = false
    var bookName = ""
    var bookUrl = ""
    var baseUrl = ""
    var bookCover = ""
    var bookIntro = ""
    var bookAuthor = ""
    var bookDetail: BookDetail? = null
}