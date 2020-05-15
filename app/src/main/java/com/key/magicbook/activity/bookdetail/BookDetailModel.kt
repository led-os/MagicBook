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

    override fun loadBookReadChapters(book: BookDetail,userName :String) {
        val find = LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ? ",
            book.bookName ,ConstantValues.BASE_URL, book.bookUrl,userName).find(BookReadChapter::class.java)
        val b = book.chapterNames.size == book.chapterUrls.size
        if(b && find.size == 0 ){
            for((index,value) in book.chapterNames.withIndex()){
                val bookReadChapter = BookReadChapter()
                bookReadChapter.bookChapterOnlyTag = book.bookName + book.bookAuthor + book.bookUrl
                bookReadChapter.chapterName =
                    filterSpecialSymbol(getChapterName(book.chapterNames.size - index,value))
                bookReadChapter.chapterUrl = book.chapterUrls[index]
                bookReadChapter.bookChapterContent = ""
                bookReadChapter.begin = 0
                bookReadChapter.chapterNum  = book.chapterNames.size - index
                bookReadChapter.userName = userName
                bookReadChapter.isLook = "false"
                bookReadChapter.isCache = "false"
                bookReadChapter.bookName = book.bookName
                bookReadChapter.baseUrl = ConstantValues.BASE_URL
                bookReadChapter.bookUrl = book.bookUrl
                bookReadChapter.save()
            }
        }else if(b && find.size < book.chapterNames.size){
            val i =  book.chapterNames.size -find.size
            for(index in 0 until i){
                val bookReadChapter = BookReadChapter()
                bookReadChapter.bookChapterOnlyTag = book.bookName + book.bookAuthor + book.bookUrl
                val value = book.chapterNames[index]
                bookReadChapter.chapterName =
                    filterSpecialSymbol(getChapterName(book.chapterNames.size - index,value))
                bookReadChapter.chapterUrl = book.chapterUrls[index]
                bookReadChapter.bookChapterContent = ""
                bookReadChapter.begin = 0
                bookReadChapter.chapterNum  = book.chapterNames.size - index
                bookReadChapter.userName = userName
                bookReadChapter.isLook = "false"
                bookReadChapter.isCache = "false"
                bookReadChapter.baseUrl = ConstantValues.BASE_URL
                bookReadChapter.bookName = book.bookName
                bookReadChapter.bookUrl = book.bookUrl
                bookReadChapter.save()
            }
        }
        val find1 = LitePal.where(
            "bookChapterOnlyTag = ?",
            book.bookName + book.bookAuthor + book.bookUrl
        ).find(BookReadChapter::class.java)
        if(find1.size > 0){
            val bookReadChapter = find1[find1.size - 1]
            if(bookReadChapter.bookChapterContent.isEmpty()){
                JsoupUtils.connectFreeUrl(bookReadChapter.chapterUrl, "#content")
                    .subscribe(object :CustomBaseObserver<Element>(){
                        override fun next(o: Element?) {
                            val contentValues = ContentValues()
                            contentValues.put("bookChapterContent","\n\u3000第"+ (find1.size - 1).toString()
                                    +"章\n\u3000" + o!!.text())
                            LitePal.updateAll(BookReadChapter::class.java,
                                contentValues,
                                "bookChapterOnlyTag = ? and chapterUrl = ?",
                                book.bookName + book.bookAuthor + book.bookUrl,
                                bookReadChapter.chapterUrl)
                        }
                    })
           }
        }

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