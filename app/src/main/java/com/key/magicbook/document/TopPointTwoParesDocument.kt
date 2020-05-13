package com.key.magicbook.document

import com.key.magicbook.db.BookDetail
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/5/11
 */
class TopPointTwoParesDocument : ParseDocument() {
    override fun parseBookDetails(document: Document): List<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun parseBookHeaders(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun parseBookDetail(document: Document): BookDetail {
        return BookDetail()
    }

    override fun parseTypeFive(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun parseTypeTwo(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun parseTypeOne(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun parseTypeSix(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun parseBookCover(document: Document): String {
        return ""
    }

    override fun parseTypeFour(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun parsePile(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun parseBookContent(document: Document): String {
        return ""
    }

    override fun getTypeNames(): MutableList<String> {
        return ArrayList<String>()
    }

    override fun parseTypeThree(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun parseTypeEight(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }

    override fun getTypeUrls(): MutableList<String> {
        return ArrayList<String>()
    }

    override fun parseTypeSeven(document: Document?): MutableList<BookDetail> {
        return ArrayList<BookDetail>()
    }
}