package com.key.magicbook.activity.read

import android.content.ContentValues
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.Toast
import com.allen.library.interceptor.Transformer
import com.key.keylibrary.bean.BusMessage
import com.key.keylibrary.utils.FileUtils
import com.key.magicbook.R
import com.key.magicbook.base.ConstantValues
import com.key.magicbook.base.CustomBaseObserver
import com.key.magicbook.base.LoadingView
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.bookpage.Config
import com.key.magicbook.bookpage.PageFactory
import com.key.magicbook.bookpage.PageWidget
import com.key.magicbook.db.BookDetail
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
    private var currentChapterPosition = 0
    private var config:Config ?= null
    private var isEnd = "false"
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


        bookpage.visibility = View.INVISIBLE
    }



    private fun getChapter(b: Boolean) {
        if(b){
            if(currentChapterPosition == 0){
                Toast.makeText(this,"您已阅读完全部内容",Toast.LENGTH_SHORT).show()
            }else{
                isEnd = "false"
                currentChapterPosition--
                loadBook()
            }
        }else{

            val find = LitePal.where(
                "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ? ",
                book!!.bookName ,ConstantValues.BASE_URL, book!!.bookUrl,getUserInfo().userName
            ).find(BookReadChapter::class.java)

            if(find.size - 1 == currentChapterPosition){
                Toast.makeText(this,"前面没有内容了喔",Toast.LENGTH_SHORT).show()
            }else{
                currentChapterPosition++
                isEnd = "true"
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
        cacheName =
            filterSpecialSymbol(book!!.bookName + book!!.bookAuthor +book!!.bookUrl)
        if(book!!.chapterNames != null){
            loadSqlData(book!!)
            val message = busMessage.message
            currentChapterPosition = busMessage.specialMessage.toInt()
            val contentValues = ContentValues()
            if(message.isNotEmpty()){
                contentValues.put("bookChapterContent", "\n\u3000第" + (book!!.chapterNames.size - currentChapterPosition).toString()
                        + "章\n\u3000" + message)
                LitePal.updateAll(BookReadChapter::class.java,contentValues,
                    "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?  and chapterNum = ? ",
                    book!!.bookName ,ConstantValues.BASE_URL, book!!.bookUrl,getUserInfo().userName,
                    (book!!.chapterNames.size - currentChapterPosition).toString())
                loadBook()
            }else{
                val toString =ConstantValues.BASE_URL +
                    book!!.chapterUrls[currentChapterPosition].toString()
                presenter!!.getBookContent( toString,currentChapterPosition)
            }

        }else{
            val find = LitePal.where(
                "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ? ",
                book!!.bookName ,ConstantValues.BASE_URL, book!!.bookUrl,getUserInfo().userName)
                .find(BookReadChapter::class.java)
            if(find.size == 0){
                JsoupUtils.getFreeDocument(ConstantValues.BASE_URL + book!!.bookUrl)
                    .subscribe(object :CustomBaseObserver<Document>(){
                        override fun next(o: Document?) {
                            val parseBookDetail = presenter!!.parseBookDetail(o!!, book!!.bookUrl)
                            presenter!!.getChapter(parseBookDetail)
                        }
                    })
            }else{
                currentChapterPosition = find.size - 1
                loadBook()
            }
        }

    }

    fun loadChapter(bookDetail: BookDetail){
        book = bookDetail
        loadSqlData(book!!)
        currentChapterPosition = bookDetail.chapterNames.size - 1
        loadBook()
    }

    private fun loadBook(){
        val find = LitePal.where(
            "bookChapterOnlyTag = ?",
            book!!.bookName + book!!.bookAuthor + book!!.bookUrl
        ).find(BookReadChapter::class.java)
        if(find[currentChapterPosition].bookChapterContent.isNotEmpty()){
            saveString(book!!.bookName,find[currentChapterPosition].bookChapterContent,cacheName)
            pageFactory!!.setPageWidget(bookpage)
            val bookList = BookList()
            bookList.bookname =  book!!.bookName
            bookList.begin = find[currentChapterPosition].begin
            toolbar.title = find[currentChapterPosition].chapterName
            bookList.charset = ""
            bookList.isEnd = isEnd
            bookList.bookpath = ConstantValues.FILE_BOOK + File.separator+ cacheName +File.separator+"/${
            book!!.bookName}.txt"
            pageFactory!!.openBook(bookList)
            handler.postDelayed({
                bookpage.visibility =View.VISIBLE
            },100)
        }else{
            getBookContent(find[currentChapterPosition].chapterUrl)
        }

    }


    private fun saveString(name :String,content: String,fileName :String){
        FileUtils.saveString(content,name,ConstantValues.FILE_BOOK+ File.separator+ fileName +File.separator)
    }


    private fun getBookContent(bookUrl :String){

        val connectFreeUrl = JsoupUtils.connectFreeUrl(
            ConstantValues.BASE_URL + bookUrl, "#content")
        connectFreeUrl
            .compose(Transformer.switchSchedulers()).
           subscribe(object : CustomBaseObserver<Element>(LoadingView(this)){
            override fun next(o: Element?) {
                hideSystemUI()
                openBook(o!!.text())
            }
        })
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
        val find = LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ? ",
            book.bookName ,ConstantValues.BASE_URL, book.bookUrl,getUserInfo().userName).find(BookReadChapter::class.java)
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
                bookReadChapter.userName = getUserInfo().userName
                bookReadChapter.chapterNum  = book.chapterNames.size - index
                bookReadChapter.isLook = "false"
                bookReadChapter.isCache = "false"
                bookReadChapter.baseUrl = ConstantValues.BASE_URL
                bookReadChapter.bookName = book.bookName
                bookReadChapter.bookUrl = book.bookUrl
                bookReadChapter.save()
            }
        }else if(b && find.size < book.chapterNames.size){
            val i = book.chapterNames.size  - find.size
            for(index in 0 until i){
                val bookReadChapter = BookReadChapter()
                bookReadChapter.bookChapterOnlyTag = book.bookName + book.bookAuthor + book.bookUrl
                val value = book.chapterNames[index]
                bookReadChapter.chapterName =
                    filterSpecialSymbol(getChapterName(book.chapterNames.size - index,value))
                bookReadChapter.chapterUrl = book.chapterUrls[index]
                bookReadChapter.bookChapterContent = ""
                bookReadChapter.begin = 0
                bookReadChapter.userName = getUserInfo().userName
                bookReadChapter.chapterNum  = book.chapterNames.size - index
                bookReadChapter.isLook = "false"
                bookReadChapter.isCache = "false"
                bookReadChapter.baseUrl = ConstantValues.BASE_URL
                bookReadChapter.bookName = book.bookName
                bookReadChapter.bookUrl = book.bookUrl
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
        Log.e("pile",content)
        val find = LitePal.where(
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ? ",
            book!!.bookName ,ConstantValues.BASE_URL, book!!.bookUrl,getUserInfo().userName
        ).find(BookReadChapter::class.java)
        val contentValues = ContentValues()
        val s =
            "\n\u3000第" + (find.size - currentChapterPosition).toString() + "章\n\u3000" + content
        contentValues.put("bookChapterContent",s )
        LitePal.updateAll(BookReadChapter::class.java,contentValues,
            "bookChapterOnlyTag = ? and chapterNum = ? ",
            book!!.bookName + book!!.bookAuthor + book!!.bookUrl,
            (find.size - currentChapterPosition).toString())
        saveString(book!!.bookName,s,cacheName)
        pageFactory!!.setPageWidget(bookpage)
        val bookList = BookList()
        bookList.bookname =  book!!.bookName
        bookList.begin = find[currentChapterPosition].begin
        toolbar.title = find[currentChapterPosition].chapterName
        bookList.charset = ""
        bookList.isEnd = isEnd
        bookList.bookpath = ConstantValues.FILE_BOOK + File.separator+ cacheName +File.separator+"/${
        book!!.bookName}.txt"
        handler.postDelayed({
            pageFactory!!.openBook(bookList)
            bookpage.visibility =View.VISIBLE
        },300)
    }

    fun bookContent(content :String,chapterPosition: Int){
        val contentValues = ContentValues()
        contentValues.put("bookChapterContent", "\n\u3000第" + (book!!.chapterNames.size - currentChapterPosition).toString()
                + "章\n\u3000" + content)
        LitePal.updateAll(BookReadChapter::class.java,contentValues,
            "bookName = ? and baseUrl = ? and bookUrl = ? and userName = ?  and chapterNum = ? ",
            book!!.bookName ,ConstantValues.BASE_URL, book!!.bookUrl,getUserInfo().userName,
            (book!!.chapterNames.size - currentChapterPosition).toString())
        loadBook()
    }
}