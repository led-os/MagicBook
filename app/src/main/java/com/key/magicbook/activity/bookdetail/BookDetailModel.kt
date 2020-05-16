package com.key.magicbook.activity.bookdetail

import android.content.ContentValues
import com.allen.library.interceptor.Transformer
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.db.BookDetail
import com.key.magicbook.db.BookLike
import com.key.magicbook.db.BookReadChapter
import com.key.magicbook.document.ParseDocumentCreator
import com.key.magicbook.jsoup.JsoupUtils
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.litepal.LitePal

/**
 * created by key  on 2020/1/5
 */
class BookDetailModel :BookDetailContract.OnModel {
    override fun parseBookDetail(document: Document,url :String): BookDetail {
        val parseDocument = ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL)
        val parseBookDetail = parseDocument.parseBookDetail(document)
        parseBookDetail.bookUrl = url
        val find = LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ?",
            parseBookDetail.bookName, parseBookDetail.baseUrl, parseBookDetail.bookUrl
        ).find(BookDetail::class.java)


        if(find.size > 0){
            val lastUpdateChapter = ContentValues()
            lastUpdateChapter.put("lastChapter", parseBookDetail.lastChapter)

            val lastUpdateChapterTime = ContentValues()
            lastUpdateChapterTime.put("lastUpdateTime", parseBookDetail.lastUpdateTime)


            LitePal.updateAll(BookDetail::class.java,lastUpdateChapter,
                "bookName = ? and baseUrl = ? and bookUrl = ?",
                parseBookDetail.bookName, parseBookDetail.baseUrl, parseBookDetail.bookUrl)

            LitePal.updateAll(BookDetail::class.java,lastUpdateChapterTime,
                "bookName = ? and baseUrl = ? and bookUrl = ?",
                parseBookDetail.bookName, parseBookDetail.baseUrl, parseBookDetail.bookUrl)
        }else{
            parseBookDetail.save()
        }
        return parseBookDetail
    }

    override fun getChapters(bookDetail: BookDetail) :Observable<String>{
       return Observable.fromArray(bookDetail.chapterElements.reversed())
            .compose(Transformer.switchSchedulers())
            .flatMap(Function<List<Element>, ObservableSource<String>> {
                Observable.create { observableEmitter ->
                    for (value in it) {
                        observableEmitter.onNext(
                            value.text() + "value" + value.select("a").attr("href")
                        )
                    }
                    observableEmitter.onComplete()
                }
            }).distinct()
    }

    override fun getBookContent(bookUrl: String, chapterPosition: Int): Observable<Element> {
       return JsoupUtils.connectFreeUrl(bookUrl, "#content")
    }

    override fun loadBookReadChapters(book: BookDetail,userName :String):Observable<Element>? {
        val find1 = LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?",
            book.bookName ,ConstantValues.BASE_URL , book.bookUrl, userName
        ).find(BookReadChapter::class.java)
        if(find1.size > 0){
            val bookReadChapter = find1[find1.size - 1]
            if(bookReadChapter.bookChapterContent.isEmpty()){
               return  JsoupUtils.connectFreeUrl(bookReadChapter.chapterUrl, "#content")
            }
         }

        return null
    }

    private fun filterSpecialSymbol(cacheName :String) :String{
        return  cacheName.replace("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
    }

    private fun getChapterName(chapterPosition :Int,value :String) :String{
        return if(value.contains("第") && value.contains("章")){
            val start = value.indexOf("第")
            val end = value.indexOf("章")
            val substring = value.substring(start, end +1)
            val replace = value.replace(substring, "")
            if(replace.trim().replace(" ","").isEmpty()){
                "第" +  chapterPosition + "章" + " " + value.
                replace("第","").
                replace("章","")
            }else{
                "第" +  chapterPosition + "章" + " " + replace
            }

        }else{
            "第" +  chapterPosition + "章" + " " + value
        }
    }
}