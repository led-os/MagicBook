package com.key.magicbook.activity.read

import android.content.ContentValues
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.Toast
import com.key.keylibrary.base.ConstantValues
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.FileUtils
import com.key.magicbook.R
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.db.BookDetail
import com.key.magicbook.bookpage.Config
import com.key.magicbook.bookpage.PageFactory
import com.key.magicbook.bookpage.PageWidget
import com.key.magicbook.db.BookLike
import com.key.magicbook.db.BookList
import com.key.magicbook.db.BookReadChapter
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.BrightnessUtil
import kotlinx.android.synthetic.main.activity_read.*
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.litepal.LitePal
import java.io.File

/**
 * created by key  on 2020/3/29
 */
class ReadActivity : MineBaseActivity<ReadPresenter>() {
    private var book : BookDetail?= null
    private var pageFactory :PageFactory ?= null
    private var cacheName = ""
    private var currentChapterName = ""
    private var currentChapterPosition = 0
    private var config:Config ?= null
    override fun createPresenter(): ReadPresenter? {
        return ReadPresenter()
    }

    override fun initView() {
        setTitle(toolbar)
        hideSystemUI()
        toolbar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }
        config = Config.createConfig(this)
        pageFactory = PageFactory.createPageFactory(this)
        bookpage.setPageMode(config!!.pageMode)
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
                controlSettingShow()
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


        read_set.setOnClickListener {
            val settingDialogFragment = SettingDialogFragment()
            settingDialogFragment.show(supportFragmentManager,"set")
            settingDialogFragment.setSettingListener(object :SettingDialogFragment.SettingListener{
                override fun changeTypeFace(typeface: Typeface?) {
                    pageFactory!!.changeTypeface(typeface)
                }

                override fun changePageMode(pageMode: Int) {
                    bookpage.setPageMode(pageMode)
                }

                override fun changeFontSize(fontSize: Int) {
                    pageFactory!!.changeFontSize(fontSize)
                }

                override fun changeSystemBright(isSystem: Boolean?, brightness: Float) {
                    if (!isSystem!!) {
                        BrightnessUtil.setBrightness(this@ReadActivity, brightness)
                    } else {
                        val bh: Int = BrightnessUtil.getScreenBrightness(this@ReadActivity)
                        BrightnessUtil.setBrightness(this@ReadActivity, bh)
                    }

                }

                override fun changeBookBg(type: Int) {
                    pageFactory!!.changeBookBg(type)
                }

            })
        }


        read_menu.setOnClickListener {
            val menuDialogFragment = MenuDialogFragment.newInstance(
                book!!.bookName + book!!.bookAuthor + book!!.bookUrl)
            menuDialogFragment.show(supportFragmentManager,"menu")
        }
    }



    private fun getChapter(b: Boolean) {
        if(b){
            if(currentChapterPosition == 0){
                Toast.makeText(this,"您已阅读完全部内容",Toast.LENGTH_SHORT).show()
            }else{
                currentChapterPosition--
                loadBook()
            }
        }else{

            val find = LitePal.where(
                "bookChapterOnlyTag = ?",
                book!!.bookName + book!!.bookAuthor + book!!.bookUrl
            ).find(BookReadChapter::class.java)

            if(find.size - 1 == currentChapterPosition){
                Toast.makeText(this,"前面没有内容了喔",Toast.LENGTH_SHORT).show()
            }else{
                currentChapterPosition++
                loadBook()
            }


        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_read
    }

    override fun receiveMessage(busMessage: BusMessage<Any>) {
        super.receiveMessage(busMessage)
        book = busMessage.data as BookDetail
        loadSqlData(book!!)
        val message = busMessage.message
        currentChapterPosition = busMessage.specialMessage.toInt()
        cacheName =
            filterSpecialSymbol(book!!.bookName + book!!.bookAuthor +book!!.bookUrl)
        val contentValues = ContentValues()
        contentValues.put("bookChapterContent", "\n\u3000第"+ (book!!.chapterNames.size - currentChapterPosition).toString()
                +"章\n\u3000"+message)
        LitePal.updateAll(BookReadChapter::class.java,contentValues,
            "bookChapterOnlyTag = ? and chapterNum = ? ",
            book!!.bookName + book!!.bookAuthor + book!!.bookUrl,
            (book!!.chapterNames.size - currentChapterPosition).toString())
        loadBook()
    }

    private fun loadBook(){
        val find = LitePal.where(
            "bookChapterOnlyTag = ?",
            book!!.bookName + book!!.bookAuthor + book!!.bookUrl
        ).find(BookReadChapter::class.java)
        if(!find[currentChapterPosition].bookChapterContent.isEmpty()){
            saveString(book!!.bookName,find[currentChapterPosition].bookChapterContent,cacheName)
            pageFactory!!.setPageWidget(bookpage)
            val bookList = BookList()
            bookList.bookname =  book!!.bookName
            bookList.begin = find[currentChapterPosition].begin
            toolbar.title = find[currentChapterPosition].chapterName
            bookList.charset = ""
            bookList.bookpath = ConstantValues.FILE_BOOK + File.separator+ cacheName +File.separator+"/${
            book!!.bookName}.txt"
            pageFactory!!.openBook(bookList)
        }else{
            getBookContent(find[currentChapterPosition].chapterUrl)
        }

    }


    private fun saveString(name :String,content: String,fileName :String){
        FileUtils.saveString(content,name,ConstantValues.FILE_BOOK+ File.separator+ fileName +File.separator)
    }


    private fun getBookContent(bookUrl :String){
        val connectFreeUrl = JsoupUtils.connectFreeUrl(
            com.key.magicbook.base.ConstantValues.BASE_URL + bookUrl, "#content")
        connectFreeUrl.subscribe(object : CustomBaseObserver<Element>(){
            override fun next(o: Element?) {
                openBook(o!!.text())
            }
        })
    }
    fun loadBookContent(document:Document){

    }

    /**
     * 隐藏菜单。沉浸式阅读
     */
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
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


    private fun filterSpecialSymbol(cacheName :String) :String{
        return  cacheName.replace("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
    }
    private fun loadSqlData(book: BookDetail){
        val b = book.chapterNames.size == book.chapterUrls.size
        val find = LitePal.where("bookChapterOnlyTag = ?",
            book.bookName + book.bookAuthor + book.bookUrl).find(BookReadChapter::class.java)

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
                bookReadChapter.isLook = "false"
                bookReadChapter.isCache = "false"
                bookReadChapter.save()
            }
        }else if(b && find.size > book.chapterNames.size){
            val i = find.size - book.chapterNames.size
            for(index in 0 .. i){
                val bookReadChapter = BookReadChapter()
                bookReadChapter.bookChapterOnlyTag = book.bookName + book.bookAuthor + book.bookUrl
                val value = book.chapterNames[index]
                bookReadChapter.chapterName =
                    filterSpecialSymbol(getChapterName(book.chapterNames.size - index,value))
                bookReadChapter.chapterUrl = book.chapterUrls[index]
                bookReadChapter.bookChapterContent = ""
                bookReadChapter.begin = 0
                bookReadChapter.chapterNum  = book.chapterNames.size - index
                bookReadChapter.isLook = "false"
                bookReadChapter.isCache = "false"
                bookReadChapter.save()
            }
        }
    }

    private fun controlSettingShow() {
        val b = toolbar.visibility == View.GONE
        if(b){
            showSystemUI()
            toolbar.visibility  = View.VISIBLE
            day.visibility = View.VISIBLE
            set.visibility = View.VISIBLE
        }else{
            hideSystemUI()
            toolbar.visibility  = View.GONE
            day.visibility = View.GONE
            set.visibility = View.GONE
        }
    }


    private fun openBook(content :String){
        val find = LitePal.where(
            "bookChapterOnlyTag = ?",
            book!!.bookName + book!!.bookAuthor + book!!.bookUrl
        ).find(BookReadChapter::class.java)
        val contentValues = ContentValues()
        contentValues.put("bookChapterContent", "\n\u3000第"+ (book!!.chapterNames.size - currentChapterPosition).toString()
                +"章\n\u3000"+ content)
        LitePal.updateAll(BookReadChapter::class.java,contentValues,
            "bookChapterOnlyTag = ? and chapterNum = ? ",
            book!!.bookName + book!!.bookAuthor + book!!.bookUrl,
            (book!!.chapterNames.size - currentChapterPosition).toString())
        saveString(book!!.bookName,find[currentChapterPosition].bookChapterContent,cacheName)
        pageFactory!!.setPageWidget(bookpage)
        val bookList = BookList()
        bookList.bookname =  book!!.bookName
        bookList.begin = find[currentChapterPosition].begin
        toolbar.title = find[currentChapterPosition].chapterName
        bookList.charset = ""
        bookList.bookpath = ConstantValues.FILE_BOOK + File.separator+ cacheName +File.separator+"/${
        book!!.bookName}.txt"
        pageFactory!!.openBook(bookList)

    }
}