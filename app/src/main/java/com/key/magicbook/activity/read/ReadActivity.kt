package com.key.magicbook.activity.read
import android.graphics.Typeface
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
import com.key.magicbook.jsoup.JsoupUtils
import com.key.magicbook.util.BrightnessUtil
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
        setTitle(toolbar)
        saveString("圣墟","test  pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {\n" +
                "            @Override\n" +
                "            public void onTimeSelect(Date date, View v) {//选中事件回调\n" +
                "\t\t\tSimpleDateFormat simpleDateFormat = new SimpleDateFormat(\"yyyy年MM月dd日-HH时MM分\");\n" +
                "\t\t\tString format = simpleDateFormat.format(date);\n" +
                "\t\t\tselect_time.setText(format);\n" +
                "} })//\n" +
                ".setType(TimePickerView.Type.ALL)//默认全部显示\n" +
                "                        .setCancelText(\"取消\")//取消按钮文字\n" +
                "                        .setSubmitText(\"确定\")//确认按钮文字\n" +
                "                        .setContentSize(18)//滚轮文字大小\n" +
                "                        .setTitleSize(20)//标题文字大小\n" +
                "                        .setTitleText(\"选择时间\")//标题文字\n" +
                "                        .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示\n" +
                "                        .isCyclic(true)//是否循环滚动\n" +
                "                        .setTitleColor(Color.BLACK)//标题文字颜色\n" +
                "                        .setSubmitColor(R.color.hui)//确定按钮文字颜色\n" +
                "                        .setCancelColor(R.color.hui)//取消按钮文字颜色\n" +
                "                        .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode\n" +
                "                        .setBgColor(0xFF333333)//滚轮背景颜色 Night mode\n" +
                "//                .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年\n" +
                "                        .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/\n" +
                "                        .setRangDate(startDate,endDate)//起始终止年月日设定\n" +
                "                        .setLabel(\"年\",\"月\",\"日\",\"时\",\"分\",\"秒\")\n" +
                "                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。\n" +
                "                        .isDialog(false)//是否显示为对话框样式\n" +
                "                        .build();\n" +
                "                pvTime.show();\n" +
                "\n" +
                "————————————————\n" +
                "版权声明：本文为CSDN博主「KeyBoarder_」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。\n" +
                "原文链接：https://blog.csdn.net/weixin_39738488/article/details/78954606  pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {\n" +
                "            @Override\n" +
                "            public void onTimeSelect(Date date, View v) {//选中事件回调\n" +
                "\t\t\tSimpleDateFormat simpleDateFormat = new SimpleDateFormat(\"yyyy年MM月dd日-HH时MM分\");\n" +
                "\t\t\tString format = simpleDateFormat.format(date);\n" +
                "\t\t\tselect_time.setText(format);\n" +
                "} })//\n" +
                ".setType(TimePickerView.Type.ALL)//默认全部显示\n" +
                "                        .setCancelText(\"取消\")//取消按钮文字\n" +
                "                        .setSubmitText(\"确定\")//确认按钮文字\n" +
                "                        .setContentSize(18)//滚轮文字大小\n" +
                "                        .setTitleSize(20)//标题文字大小\n" +
                "                        .setTitleText(\"选择时间\")//标题文字\n" +
                "                        .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示\n" +
                "                        .isCyclic(true)//是否循环滚动\n" +
                "                        .setTitleColor(Color.BLACK)//标题文字颜色\n" +
                "                        .setSubmitColor(R.color.hui)//确定按钮文字颜色\n" +
                "                        .setCancelColor(R.color.hui)//取消按钮文字颜色\n" +
                "                        .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode\n" +
                "                        .setBgColor(0xFF333333)//滚轮背景颜色 Night mode\n" +
                "//                .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年\n" +
                "                        .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/\n" +
                "                        .setRangDate(startDate,endDate)//起始终止年月日设定\n" +
                "                        .setLabel(\"年\",\"月\",\"日\",\"时\",\"分\",\"秒\")\n" +
                "                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。\n" +
                "                        .isDialog(false)//是否显示为对话框样式\n" +
                "                        .build();\n" +
                "                pvTime.show();\n" +
                "\n" +
                "————————————————\n" +
                "版权声明：本文为CSDN博主「KeyBoarder_」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。\n" +
                "原文链接：https://blog.csdn.net/weixin_39738488/article/details/78954606","test")
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
            val menuDialogFragment = MenuDialogFragment()
            menuDialogFragment.show(supportFragmentManager,"menu")
        }
    }

    private fun controlSettingShow() {
        val b = toolbar.visibility == View.GONE
        if(b){
            toolbar.visibility  = View.VISIBLE
            day.visibility = View.VISIBLE
            set.visibility = View.VISIBLE
        }else{
            toolbar.visibility  = View.GONE
            day.visibility = View.GONE
            set.visibility = View.GONE
        }
    }

    private fun getChapter(b: Boolean) {
        var index :Int = 0
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
                loadBook(book!!,"",book!!.chapterNames[index-1],b)
            }else{
                Toast.makeText(this,"当前章节为最后一章",Toast.LENGTH_SHORT).show()
            }


        }else{
            //pre
            if(book != null ){
                if( book!!.chapterUrls != null){
                    if(book!!.chapterUrls.size-1 >= index +1){
                        loadBook(book!!,"",book!!.chapterNames[index+1],b)
                    }else{
                        Toast.makeText(this,"当前章节为第一章",Toast.LENGTH_SHORT).show()
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