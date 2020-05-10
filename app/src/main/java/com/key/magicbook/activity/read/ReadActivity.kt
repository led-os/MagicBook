package com.key.magicbook.activity.read
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
import com.key.magicbook.bean.BookDetail
import com.key.magicbook.bookpage.Config
import com.key.magicbook.bookpage.PageFactory
import com.key.magicbook.bookpage.PageWidget
import com.key.magicbook.db.BookList
import com.key.magicbook.db.BookReadChapter
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.BrightnessUtil
import kotlinx.android.synthetic.main.activity_read.*
import org.jsoup.nodes.Element
import org.litepal.LitePal
import java.io.File

/**
 * created by key  on 2020/3/29
 */
class ReadActivity : MineBaseActivity<ReadPresenter>() {
    private var book : BookDetail ?= null
    private var pageFactory :PageFactory ?= null
    private var cacheName = ""
    private var currentChapterName = ""
    private var isOpen = false
    private var bookCacheName = "";
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
        saveString("圣墟","test","test")
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
        if(book == null){
            pageFactory!!.setPageWidget(bookpage)
            val bookList = BookList()
            bookList.bookname = "圣墟"
            bookList.begin = 0
            bookList.charset = ""
            bookList.bookpath = ConstantValues.FILE_BOOK + File.separator + "test"+File.separator+"/圣墟.txt"
            pageFactory!!.openBook(bookList)
        }



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

    private fun getChapter(b: Boolean) {
        var index = 0
        if(book != null){
            if(book!!.chapterNames != null){
                if(book!!.chapterNames.size > 0){
                    for(value in  book!!.chapterNames){
                        if(value == currentChapterName){
                            break
                        }
                        index++
                    }
                }
            }
        }


        if(b){
            if(index -1 >= 0){
                loadBook(book!!,false)
            }else{
                Toast.makeText(this,"您已阅读完全部内容",Toast.LENGTH_SHORT).show()
            }


        }else{
            if(book != null ){
                if( book!!.chapterUrls != null){
                    if(book!!.chapterUrls.size-1 >= index +1){
                        loadBook(book!!,false)
                    }else{
                        Toast.makeText(this,"前面没有内容了喔",Toast.LENGTH_SHORT).show()
                    }
                }

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
        loadBook(bookDetail,true)
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


    private fun loadBook(book :BookDetail,isFirstLoad :Boolean){
        if(isFirstLoad){
            bookCacheName = filterSpecialSymbol(book.bookName + book.bookAuthor + book.bookUrl)
            cacheName = book.bookName + book.bookAuthor
            cacheName = filterSpecialSymbol(cacheName)
            this.book = book
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

        pageFactory!!.setPageWidget(bookpage)

        val bookList = BookList()
        bookList.bookname =  book.bookName
        bookList.begin = 0
        toolbar.title = ""
        bookList.charset = ""
        bookList.bookpath = ConstantValues.FILE_BOOK + File.separator+ cacheName +File.separator+"/${
           bookCacheName}.txt"
        pageFactory!!.openBook(bookList)


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
            "第" +  chapterPosition + "章" + " " + replace
        }else{
            "第" +  chapterPosition + "章" + " " + value
        }
    }
    private fun saveString(name :String,content: String,fileName :String){
        FileUtils.saveString(content,name,ConstantValues.FILE_BOOK+ File.separator+ fileName +File.separator)
    }


    private fun getBookContent(bookUrl :String,chapter :String){
        val connectFreeUrl = JsoupUtils.connectFreeUrl(com.key.magicbook.base.ConstantValues.BASE_URL +bookUrl, "#content")
        connectFreeUrl.subscribe(object : CustomBaseObserver<Element>(){
            override fun next(o: Element?) {
                val s = book!!.bookName
                saveString(s,o!!.text(),cacheName)
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
}