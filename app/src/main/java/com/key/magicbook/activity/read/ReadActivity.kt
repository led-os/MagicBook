package com.key.magicbook.activity.read

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.key.keylibrary.base.ConstantValues
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.FileUtils
import com.key.magicbook.R
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.bean.BookList
import com.key.magicbook.bookpage.Config
import com.key.magicbook.bookpage.PageFactory
import com.key.magicbook.bookpage.PageWidget
import com.key.magicbook.jsoup.JsoupUtils
import kotlinx.android.synthetic.main.activity_read.*
import org.jsoup.nodes.Element
import java.io.File

/**
 * created by key  on 2020/3/29
 */
class ReadActivity : MineBaseActivity<ReadPresenter>() {
    private var book : BookDetail ?= null
    private var pageFactory :PageFactory ?= null
    private var cacheName = ""
    private var currentChapterName = ""
    override fun createPresenter(): ReadPresenter? {
        return ReadPresenter()
    }

    override fun initView() {
        saveString("圣墟","112233","test")
        val config = Config.createConfig(this)
        pageFactory = PageFactory.createPageFactory(this)
        bookpage.setPageMode(config.pageMode)
        bookpage.setTouchListener(object : PageWidget.TouchListener{
            override fun up(next: Boolean) {
               if(next && pageFactory!!.isLastPage){
                   getChapter(true)
               }else if(!next && pageFactory!!.isFirstPage){
                   getChapter(false)
               }

            }

            override fun prePage(): Boolean {
                pageFactory!!.prePage()
                if (pageFactory!!.isFirstPage) {
                    return false
                }
                return true
            }

            override fun center() {

            }

            override fun cancel() {

            }

            override fun nextPage(): Boolean {
                pageFactory!!.nextPage()
                if (pageFactory!!.isLastPage) {
                    return false
                }
                return true
            }

        })
        if(book == null){
            pageFactory!!.setPageWidget(bookpage)
            val bookList = BookList()
            bookList.bookname = "圣墟"
            bookList.begin = 0
            bookList.charset = ""
            bookList.bookpath = ConstantValues.FILE_BOOK+File.separator + "test"+File.separator+"/圣墟.txt"
            pageFactory!!.openBook(bookList)
        }
    }

    private fun getChapter(b: Boolean) {
        var index :Int = 0
        for(value in  book!!.chapterNames){
            if(value == currentChapterName){
                break
            }
            index++
        }



        if(b){
            //next
            if(index -1 >= 0){
                loadBook(book!!,"",book!!.chapterNames[index-1],b)
            }else{
                Toast.makeText(this,"当前章节为最后一章",Toast.LENGTH_SHORT).show()
            }


        }else{
            //pre
            if(book!!.chapterUrls.size-1 >= index +1){
                loadBook(book!!,"",book!!.chapterNames[index+1],b)
            }else{
                Toast.makeText(this,"当前章节为第一章",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_read
    }

    override fun receiveMessage(busMessage: BusMessage<Any>) {
        super.receiveMessage(busMessage)
        val bookDetail = busMessage.data as BookDetail
        val message = busMessage.message
        val chapter = busMessage.specialMessage
        loadBook(bookDetail,message,chapter,true)
    }

    private fun cacheBook(bookDetail: BookDetail, message: String?) {
        var index :Int = 0
        for(value in  bookDetail.chapterNames){
            if(value == message){
                break
            }
            index++
        }
        if(index -1 >= 0){
            getBookContent(book!!.chapterUrls[index-1],book!!.chapterNames[index-1])
        }

        if(book!!.chapterUrls.size-1 >= index +1){
            getBookContent(book!!.chapterUrls[index+1],book!!.chapterNames[index+1])
        }
    }


    private fun loadBook(book :BookDetail,content:String,chapter :String,isFirstPage :Boolean){
        val s = book.bookName + chapter
        currentChapterName = chapter
        cacheName = book.bookName + book.bookAuthor
        cacheName = cacheName.replace("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        this.book = book
        val file =
            File(ConstantValues.FILE_BOOK + File.separator + cacheName + File.separator + "/$s.txt")
        if(!file.exists()){
            saveString(s,content,cacheName)
        }
        cacheBook(book,chapter)
        pageFactory!!.setPageWidget(bookpage)
        val bookList = BookList()
        bookList.bookname =  chapter
        if(isFirstPage){
            bookList.begin = 0
        }else{
            if(file.exists()){
                bookList.begin = pageFactory!!.bookLen
            }
        }

        bookList.charset = ""
        bookList.bookpath = ConstantValues.FILE_BOOK+ File.separator+ cacheName +File.separator+"/$s.txt"
        pageFactory!!.openBook(bookList)
    }
    private fun saveString(name :String,content: String,fileName :String){
        FileUtils.saveString(content,name,ConstantValues.FILE_BOOK+ File.separator+ fileName +File.separator)
    }


    private fun getBookContent(bookUrl :String,chapter :String){
        val connectFreeUrl = JsoupUtils.connectFreeUrl("https://www.dingdiann.com/$bookUrl", "#content")
        connectFreeUrl.subscribe(object : CustomBaseObserver<Element>(){
            override fun next(o: Element?) {
                val s = book!!.bookName + chapter
                saveString(s,o!!.text(),cacheName)
            }
        })
    }
}