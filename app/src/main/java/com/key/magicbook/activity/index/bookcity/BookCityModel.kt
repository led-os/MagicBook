package com.key.magicbook.activity.index.bookcity

import android.text.TextUtils
import com.allen.library.interceptor.Transformer
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.jsoup.JsoupUtils.getFreeDocument
import io.reactivex.Observable
import org.jsoup.nodes.Document
import kotlin.collections.ArrayList

/**
 * created by key  on 2020/5/10
 */
class BookCityModel:BookCityContract.OnModel {
    override fun loadDocument(url: String): Observable<Document> {
        return getFreeDocument(ConstantValues.BASE_URL)
            .compose(Transformer.switchSchedulers())
    }


    override fun loadPile(document: Document): List<BookDetail> {
        val books :ArrayList<BookDetail> = ArrayList()
        val select = document!!.select("#hotcontent > div.l > div")
        for (value in select) {
            val bookDetail = BookDetail()
            val img =
                value.select("div.image > a > img")
                    .attr("src")
            val name =
                value.select("dl > dt > a")
                    .text()
            val url =
                value.select("dl > dt > a")
                    .attr("href")
            val intro = value.select("dl > dd").text()
            bookDetail.bookIntro = intro
            bookDetail.bookCover = img
            bookDetail.bookName = name
            bookDetail.bookUrl = url
            if (!TextUtils.isEmpty(intro)) {
                books!!.add(bookDetail)
            }
        }
        val select1 = document!!.select("#novelslist1 > div")
        for (value in select1) {
            val bookDetail = BookDetail()

            val img = value.select(" div > div.image > img").attr("src")
            val name = value.select(" div > dl > dt > a").text()
            val url = value.select(" div > dl > dt > a").attr("href")
            val intro = value.select("div > dl > dd").text()

            bookDetail.bookIntro = intro
            bookDetail.bookCover = img
            bookDetail.bookName = name
            bookDetail.bookUrl = url

            if (!TextUtils.isEmpty(intro)) {
                books!!.add(bookDetail)
            }
        }

        val select2 = document!!.select("#novelslist2 > div")
        for (value in select2) {
            val bookDetail = BookDetail()
            val img = value.select(" div > div.image > img").attr("src")
            val name = value.select(" div > dl > dt > a").text()
            val url = value.select(" div > dl > dt > a").attr("href")
            val intro = value.select("div > dl > dd").text()

            bookDetail.bookIntro = intro
            bookDetail.bookCover = img
            bookDetail.bookName = name
            bookDetail.bookUrl = url
            if (!TextUtils.isEmpty(intro)) {
                books!!.add(bookDetail)
            }
        }
        return books
    }

    override fun loadTypeOne(document: Document): List<BookDetail> {
        val scriptures = document!!.select("#hotcontent > div.r > ul > li")
        var scripturesDetails =ArrayList<BookDetail>()
        for(value in scriptures){

            val bookName =  value.select("span.s2 > a").text()
            val attr = value.select("span.s2 > a")
                .attr("href")
            val authorName = value.select("span.s5").text()
            val bookDetail = BookDetail()
            bookDetail.bookName = bookName
            bookDetail.bookUrl = attr
            bookDetail.bookAuthor = authorName
            bookDetail.bookType = "经典推荐"
            scripturesDetails.add(bookDetail)

        }
        return scripturesDetails
    }

    override fun loadTypeTwo(document: Document): List<BookDetail> {
        val select = document!!.select("#novelslist1 > div")
        val select1 = select[0].select("ul > li")
        val bookDetails = ArrayList<BookDetail>()
        for(value in select1){
            val all = value.text()
            val bookName = value.select("a").text()
            val attr = value.select("a").attr("href")
            val authorName = all.replace(" ", "")
                .replace("/", "").replace(bookName, "")
            val bookDetail = BookDetail()
            bookDetail.bookName = bookName
            bookDetail.bookUrl = attr
            bookDetail.bookAuthor = authorName
            bookDetail.bookType = "玄幻奇幻"
            bookDetails.add(bookDetail)
        }
        return bookDetails
    }

    override fun loadTypeThree(document: Document): List<BookDetail> {
        val select = document!!.select("#novelslist1 > div")
        val select1 = select[1].select("ul > li")
        val bookDetails = ArrayList<BookDetail>()
        for(value in select1){
            val all = value.text()
            val bookName = value.select("a").text()
            val attr = value.select("a").attr("href")
            val authorName = all.replace(" ", "")
                .replace("/", "").replace(bookName, "")
            val bookDetail = BookDetail()
            bookDetail.bookName = bookName
            bookDetail.bookUrl = attr
            bookDetail.bookAuthor = authorName
            bookDetail.bookType = "武侠仙侠"
            bookDetails.add(bookDetail)
        }
        return bookDetails
    }

    override fun loadTypeFour(document: Document): List<BookDetail> {
        val select = document!!.select("#novelslist1 > div")
        val select1 = select[2].select("ul > li")
        val bookDetails = ArrayList<BookDetail>()
        for(value in select1){
            val all = value.text()
            val bookName = value.select("a").text()
            val attr = value.select("a").attr("href")
            val authorName = all.replace(" ", "")
                .replace("/", "").replace(bookName, "")
            val bookDetail = BookDetail()
            bookDetail.bookName = bookName
            bookDetail.bookUrl = attr
            bookDetail.bookAuthor = authorName
            bookDetail.bookType = "都市言情"
            bookDetails.add(bookDetail)
        }
        return bookDetails
    }

    override fun loadTypeFive(document: Document): List<BookDetail> {
        val select2 = document!!.select("#novelslist2 > div")
        val select1 = select2[0].select("ul > li")
        val bookDetailNovel = ArrayList<BookDetail>()
        for(value in select1) {
            val all = value.text()
            val bookName = value.select("a").text()
            val attr = value.select("a").attr("href")
            val authorName = all.replace(" ", "")
                .replace("/", "").replace(bookName, "")
            val bookDetail = BookDetail()
            bookDetail.bookName = bookName
            bookDetail.bookUrl = attr
            bookDetail.bookAuthor = authorName
            bookDetail.bookType = "历史军事"
            bookDetailNovel.add(bookDetail)
        }
        return bookDetailNovel
    }

    override fun loadTypeSix(document: Document): List<BookDetail> {
        val select2 = document!!.select("#novelslist2 > div")
        val select1 = select2[1].select("ul > li")
        val bookDetailNovel = ArrayList<BookDetail>()
        for(value in select1) {
            val all = value.text()
            val bookName = value.select("a").text()
            val attr = value.select("a").attr("href")
            val authorName = all.replace(" ", "")
                .replace("/", "").replace(bookName, "")
            val bookDetail = BookDetail()
            bookDetail.bookName = bookName
            bookDetail.bookUrl = attr
            bookDetail.bookAuthor = authorName
            bookDetail.bookType = "科幻灵异"
            bookDetailNovel.add(bookDetail)
        }
        return bookDetailNovel
    }

    override fun loadTypeSeven(document: Document): List<BookDetail> {
        val select2 = document!!.select("#novelslist2 > div")
        val select1 = select2[2].select("ul > li")
        val bookDetailNovel = ArrayList<BookDetail>()
        for(value in select1) {
            val all = value.text()
            val bookName = value.select("a").text()
            val attr = value.select("a").attr("href")
            val authorName = all.replace(" ", "")
                .replace("/", "").replace(bookName, "")
            val bookDetail = BookDetail()
            bookDetail.bookName = bookName
            bookDetail.bookUrl = attr
            bookDetail.bookAuthor = authorName
            bookDetail.bookType = "网游竞技"
            bookDetailNovel.add(bookDetail)
        }
        return bookDetailNovel
    }

    override fun loadTypeEight(document: Document): List<BookDetail> {
        val new = document!!.select("#newscontent > div.r > ul > li")
        val bookDetails = ArrayList<BookDetail>()
        for(value in new){
            val bookName =  value.select("span.s2 > a").text()
            val attr = value.select("span.s2 > a").attr("href")
            val authorName = value.select("span.s5").text()
            val bookDetail = BookDetail()
            bookDetail.bookName = bookName
            bookDetail.bookUrl = attr
            bookDetail.bookAuthor = authorName
            bookDetail.bookType = "新书"
            bookDetails.add(bookDetail)
        }
       return bookDetails
    }
}