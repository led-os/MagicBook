package com.key.magicbook.bookpage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.key.keylibrary.utils.FileUtils;
import com.key.keylibrary.utils.UiUtils;
import com.key.magicbook.R;
import com.key.magicbook.db.BookCatalogue;
import com.key.magicbook.db.BookList;
import com.key.magicbook.bean.TRPage;
import com.key.magicbook.util.BitmapUtil;
import com.key.magicbook.util.BookUtil;
import com.key.magicbook.util.CommonUtil;

import org.litepal.LitePal;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class PageFactory {

    private boolean isEnd = false;
    private static final String TAG = "PageFactory";
    private static PageFactory pageFactory;
    private Context mContext;
    /**
     * Config 字体大小颜色背景的配置类
     */
    private Config config;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    /**
     * 页面宽高
     * @params mWidth
     * @params mHeight
     */
    private int mWidth;
    private int mHeight;

    /**
     * @params m_fontSize 文字字体大小
     * @params sdf 时间格式
     * @params date 时间
     * @params df 进度格式
     * @params mBorderWidth 电池边界宽度
     * @params marginHeight 上下与边缘的距离
     * @params measureMarginWidth 左右与边缘的距离
     * @params marginWidth 左右与边缘的距离
     * @params statusMarginBottom 状态栏距离底部高度
     * @params lineSpace 行间距
     * @params paragraphSpace 段间距
     * @params fontHeight 字高度
     * @params typeface 字体
     */
    private float m_fontSize ;
    private SimpleDateFormat sdf;
    private String date;
    private DecimalFormat df ;
    private float mBorderWidth;
    private float marginHeight ;
    private float measureMarginWidth ;
    private float marginWidth ;
    private float statusMarginBottom;
    private float lineSpace;
    private float paragraphSpace;
    private float fontHeight;
    private Typeface typeface;


    /**
     * @params mPaint 文字画笔
     * @params waitPaint 加载画笔
     * @params m_textColor 文字颜色
     * @params mVisibleHeight 绘制内容的高
     * @params mVisibleWidth 绘制内容的宽
     * @params mLineCount 每页可以显示的行数
     */
    private Paint mPaint;
    private Paint waitPaint;
    private int m_textColor = Color.rgb(50, 65, 78);
    private float mVisibleHeight;
    private float mVisibleWidth;
    private int mLineCount;


    /**
     * @params mBatteryPaint 电池画笔
     * @params mBatteryFontSize 电池字体大小
     * @params m_book_bg 背景图片
     * @params mBatteryPercentage 电池电量百分比
     * @params rect1 电池外边框
     * @params rect2 电池内边框
     * @params level 当前电量
     * @params m_isFirstPage 当前是否为第一页
     * @params m_isLastPage 当前是否为最后一页
     */
    private Paint mBatteryPaint;
    private float mBatteryFontSize;
    private Bitmap m_book_bg = null;
    private Intent batteryInfoIntent;
    private float mBatteryPercentage;
    private RectF rect1 = new RectF();
    private RectF rect2 = new RectF();
    private boolean m_isFirstPage;
    private boolean m_isLastPage;
    private int level = 0;

    /**
     * @params mBookPageWidget 书本widget
     * @params currentProgress 现在的进度
     * @params bookPath 书本路径
     * @params bookName 书本名字
     * @params bookList 书本列表
     * @params currentCharter 书本章节
     */
    private PageWidget mBookPageWidget;
    private float currentProgress;
    private String bookPath = "";
    private String bookName = "";
    private BookList bookList;
    private int currentCharter = 0;


    /**
     *  @params currentPage 当前页
     *  @params prePage 上一页或下一页
     *  @params cancelPage 取消当页
     */
    private TRPage currentPage;
    private TRPage prePage;
    private TRPage cancelPage;


    /**
     * @params bookTask 打开书本后台任务
     * @params mBookUtil 打开书本的工具
     * @params mPageEvent 阅读进度监听
     * @params values ContentValues (key - value) the key
     */
    private BookTask bookTask;
    private BookUtil mBookUtil;
    private PageEvent mPageEvent;
    ContentValues values = new ContentValues();

    /**
     * 书本状态
     */
    private static Status mStatus = Status.OPENING;

    /**
     * 书本打开状态
     * @params OPENING 打开
     * @params FINISH 完成
     * @params FAIL 失败
     */
    public enum Status {
        OPENING,
        FINISH,
        FAIL,
    }


    /**
     *  懒汉式单例 pageFactory
     * @return
     */
    public static synchronized PageFactory getInstance(){
        return pageFactory;
    }

    public static synchronized PageFactory createPageFactory(Context context){
        if (pageFactory == null){
            pageFactory = new PageFactory(context);
        }
        return pageFactory;
    }


    /**
     *  初始化画笔等界面属性
     * @param context
     */
    private PageFactory(Context context) {
        mBookUtil = new BookUtil();
        mContext = context.getApplicationContext();
        config = Config.getInstance();

        /**
         * 获取屏幕宽高
         */
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        mWidth = metric.widthPixels;
        mHeight = metric.heightPixels;

        sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        date = sdf.format(new java.util.Date());
        df = new DecimalFormat("#0.0");


        /**
         * 间隔宽度初始化
         */
        marginWidth = mContext.getResources().getDimension(R.dimen.readingMarginWidth);
        marginHeight = mContext.getResources().getDimension(R.dimen.readingMarginHeight);
        statusMarginBottom = mContext.getResources().getDimension(R.dimen.reading_status_margin_bottom);
        lineSpace = context.getResources().getDimension(R.dimen.reading_line_spacing);
        paragraphSpace = context.getResources().getDimension(R.dimen.reading_paragraph_spacing);


        /**
         * 可提供绘制文字的区域宽高
         */
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;


        /**
         * 文字相关属性，提供字体，字号，文字颜色
         * 为文字画笔的初始化提供参数
         *
         *  ANTI_ALIAS_FLAG ：消除锯齿
         */
        typeface = config.getTypeface();
        m_fontSize = config.getFontSize();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
        mPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        mPaint.setTextSize(m_fontSize);// 字体大小
        mPaint.setColor(m_textColor);// 字体颜色
        mPaint.setTypeface(typeface);
        mPaint.setSubpixelText(true);// 设置该项为true，将有助于文本在LCD屏幕上的显示效果


        /**
         *等待状态文字画笔初始化
         */
        waitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
        waitPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        waitPaint.setTextSize(mContext.getResources().getDimension(R.dimen.reading_max_text_size));// 字体大小
        waitPaint.setColor(m_textColor);// 字体颜色
        waitPaint.setTypeface(typeface);
        waitPaint.setSubpixelText(true);// 设置该项为true，将有助于文本在LCD屏幕上的显示效果

        /**
         * 计算一页中的行数
         */
        calculateLineCount();


        /**
         * 电池画笔初始化
         */
        mBorderWidth = mContext.getResources().getDimension(R.dimen.reading_board_battery_border_width);
        mBatteryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBatteryFontSize = CommonUtil.sp2px(context, 12);
        mBatteryPaint.setTextSize(mBatteryFontSize);
        mBatteryPaint.setTypeface(typeface);
        mBatteryPaint.setTextAlign(Paint.Align.LEFT);
        mBatteryPaint.setColor(m_textColor);
        batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ;//注册广播,随时获取到电池电量信息


        /**
         * 初始化背景
         */
        initBg(config.getDayOrNight());

        /**
         * 测量左右边距
         */
        measureMarginWidth();
    }

    /**
     * |u3000  表示空格
     */
    private void measureMarginWidth(){
        float wordWidth =mPaint.measureText("\u3000");
        float width = mVisibleWidth % wordWidth;
        measureMarginWidth = marginWidth + width / 2;
    }

    /**
     * 初始化背景
     * @param isNight 白天,黑夜
     */
    private void initBg(Boolean isNight){
        if (isNight) {
            //设置背景
            Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.BLACK);
            setBgBitmap(bitmap);
            //设置字体颜色
            setM_textColor(Color.rgb(128, 128, 128));
            setBookPageBg(Color.BLACK);
        } else {
            //设置背景
            setBookBg(config.getBookBgType());
        }
    }


    /**
     *  计算可显示行数
     */
    private void calculateLineCount(){
        mLineCount = (int) (mVisibleHeight / (m_fontSize + lineSpace));// 可显示的行数
    }


    /**
     * 绘制初始状态
     * @param bitmap
     */
    private void drawStatus(Bitmap bitmap){
        String status = "";
        switch (mStatus){
            case OPENING:
                status = "";
                break;
            case FAIL:
                status = "fail";
                break;
        }

        Canvas c = new Canvas(bitmap);
        c.drawBitmap(getBgBitmap(), 0, 0, null);
        waitPaint.setColor(getTextColor());
        waitPaint.setTextAlign(Paint.Align.CENTER);
        Rect targetRect = new Rect(0, 0, mWidth, mHeight);
        Paint.FontMetricsInt fontMetrics = waitPaint.getFontMetricsInt();
        // 转载请注明出处：http://blog.csdn.net/hursing
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
//        waitPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText(status, targetRect.centerX(), baseline, waitPaint);
        mBookPageWidget.postInvalidate();
    }


    /**
     *  绘制界面
     * @param bitmap
     * @param m_lines  文字行数
     * @param updateCharter  更新章节
     */
    public void onDraw(Bitmap bitmap,List<String> m_lines,Boolean updateCharter) {
        if (getDirectoryList().size() > 0 && updateCharter) {
            currentCharter = getCurrentCharter();
        }
        //更新数据库进度
//        if (currentPage != null && bookList != null){
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    values.put("begin",currentPage.getBegin());
//                    LitePal.update(BookList.class,values,bookList.getId());
//                }
//            }.start();
//        }

        Canvas c = new Canvas(bitmap);
        c.drawBitmap(getBgBitmap(), 0, 0, null);
        mPaint.setTextSize(getFontSize());
        mPaint.setColor(getTextColor());
        mBatteryPaint.setColor(getTextColor());
        if (m_lines.size() == 0) {
            return;
        }


        /**
         *  逐行绘制文字
         */
        if (m_lines.size() > 0) {
            float y = marginHeight;
            for (String strLine : m_lines) {
                y += m_fontSize + lineSpace;
                c.drawText(strLine, measureMarginWidth, y, mPaint);
            }
        }




        //画进度及时间
        int dateWith = (int) (mBatteryPaint.measureText(date)+mBorderWidth);//时间宽度
        float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//进度
        currentProgress = fPercent;
        if (mPageEvent != null){
            mPageEvent.changeProgress(fPercent);
        }
        String strPercent = df.format(fPercent * 100) + "%";//进度文字
        int nPercentWidth = (int) mBatteryPaint.measureText("999.9%") + 1;  //Paint.measureText直接返回參數字串所佔用的寬度
   //     c.drawText(strPercent, mWidth - nPercentWidth, mHeight - statusMarginBottom, mBatteryPaint);//x y为坐标值
        c.drawText(date, marginWidth ,mHeight - statusMarginBottom, mBatteryPaint);





        // 画电池
        level = batteryInfoIntent.getIntExtra( "level" , 0 );
        int scale = batteryInfoIntent.getIntExtra("scale", 100);
        mBatteryPercentage = (float) level / scale;
        float rect1Left = marginWidth + dateWith + statusMarginBottom;//电池外框left位置





        //画电池外框
        float width = CommonUtil.convertDpToPixel(mContext,20) - mBorderWidth;
        float height = CommonUtil.convertDpToPixel(mContext,10);
        rect1.set(rect1Left, mHeight - height - statusMarginBottom,rect1Left + width, mHeight - statusMarginBottom);
        rect2.set(rect1Left + mBorderWidth, mHeight - height + mBorderWidth - statusMarginBottom, rect1Left + width - mBorderWidth, mHeight - mBorderWidth - statusMarginBottom);
        c.save();
        c.clipRect(rect2, Region.Op.DIFFERENCE);
        c.drawRect(rect1, mBatteryPaint);
        c.restore();




        //画电量部分
        rect2.left += mBorderWidth;
        rect2.right -= mBorderWidth;
        rect2.right = rect2.left + rect2.width() * mBatteryPercentage;
        rect2.top += mBorderWidth;
        rect2.bottom -= mBorderWidth;
        c.drawRect(rect2, mBatteryPaint);


        //画电池头
        int poleHeight = (int) CommonUtil.convertDpToPixel(mContext,10) / 2;
        rect2.left = rect1.right;
        rect2.top = rect2.top + poleHeight / 4;
        rect2.right = rect1.right + mBorderWidth;
        rect2.bottom = rect2.bottom - poleHeight/4;
        c.drawRect(rect2, mBatteryPaint);



        //+ UiUtils.getStateBar(UiUtils.getContext()

        //画书名
        c.drawText(CommonUtil.subString(bookName,12), marginWidth ,statusMarginBottom + mBatteryFontSize, mBatteryPaint);


        //画章
        if (getDirectoryList().size() > 0) {
            String charterName = CommonUtil.subString(getDirectoryList().get(currentCharter).getBookCatalogue(),12);
            int nChaterWidth = (int) mBatteryPaint.measureText(charterName) + 1;
            c.drawText(charterName, mWidth - marginWidth - nChaterWidth, statusMarginBottom  + mBatteryFontSize, mBatteryPaint);
        }


        /**
         *  通知视图改变
         */
        mBookPageWidget.postInvalidate();
    }

    /**
     * 向前翻页
     */
    public void prePage(){
        if (currentPage.getBegin() <= 0) {
            if (!m_isFirstPage){

            }
            m_isFirstPage = true;
            return;
        } else {
            m_isFirstPage = false;
        }

        cancelPage = currentPage;
        onDraw(mBookPageWidget.getCurPage(),currentPage.getLines(),true);
        currentPage = getPrePage();
        onDraw(mBookPageWidget.getNextPage(),currentPage.getLines(),true);
    }


    /**
     * 向后翻页
     */
    public void nextPage(){
        if (currentPage.getEnd() >= mBookUtil.getBookLen()) {
            if (!m_isLastPage){
                //lastPage()
            }
            m_isLastPage = true;
            return;
        } else {
            m_isLastPage = false;
        }

        cancelPage = currentPage;
        onDraw(mBookPageWidget.getCurPage(),currentPage.getLines(),true);
        prePage = currentPage;
        currentPage = getNextPage();
        onDraw(mBookPageWidget.getNextPage(),currentPage.getLines(),true);
    }

    /**
     * 取消翻页
     */
    public void cancelPage(){
        currentPage = cancelPage;
    }

    /**
     * 打开书本
     * @throws IOException
     */
    public void openBook(BookList bookList) throws IOException {
        //清空数据
        currentCharter = 0;
        //m_mbBufLen = 0;
        initBg(config.getDayOrNight());

        this.bookList = bookList;
        bookPath = bookList.getBookpath();

        isEnd = bookList.getIsEnd().equals("true");
        //根据文件名称决定书名
        bookName = FileUtils.getFileName(bookPath);



//        mStatus = Status.OPENING;
//        drawStatus(mBookPageWidget.getCurPage());
//        drawStatus(mBookPageWidget.getNextPage());


        /**
         *  重置打开书本的任务
         */
        if (bookTask != null && bookTask.getStatus() != AsyncTask.Status.FINISHED){
            bookTask.cancel(true);
        }
        /**
         *  new Task for open book
         */
        bookTask = new BookTask();
        bookTask.execute(bookList.getBegin());
    }

    private class BookTask extends AsyncTask<Long,Void,Boolean>{
        private long begin = 0;
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (isCancelled()){
                return;
            }
            if (result) {
                PageFactory.mStatus = PageFactory.Status.FINISH;
                if(isEnd){
                    long allNextLines = getAllNextLines();
                    currentPage = getPageForBegin(allNextLines);
                }else{
                    currentPage = getPageForBegin(0);
                }

                if (mBookPageWidget != null) {
                    currentPage(true);
                }

            }else{
                /**
                 * 更新书本状态
                 */
//                PageFactory.mStatus = PageFactory.Status.FAIL;
//                drawStatus(mBookPageWidget.getCurPage());
//                drawStatus(mBookPageWidget.getNextPage());
                Toast.makeText(mContext,"打开书本失败！",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Long... params) {
            begin = params[0];
            try {
                mBookUtil.setPosition(0);
                mBookUtil.openBook(bookList);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

    }

    public TRPage getNextPage(){
        mBookUtil.setPosition(currentPage.getEnd());
        TRPage trPage = new TRPage();
        trPage.setBegin(currentPage.getEnd() + 1);
        trPage.setLines(getNextLines());
        trPage.setEnd(mBookUtil.getPosition());
        return trPage;
    }

    public TRPage getPrePage(){
        mBookUtil.setPosition(currentPage.getBegin());
        TRPage trPage = new TRPage();
        trPage.setEnd(mBookUtil.getPosition() - 1);
        trPage.setLines(getPreLines());
        trPage.setBegin(mBookUtil.getPosition());
        return trPage;
    }

    public TRPage getPageForBegin(long begin){
        TRPage trPage = new TRPage();
        trPage.setBegin(begin);
        mBookUtil.setPosition(begin - 1);
        trPage.setLines(getNextLines());
        trPage.setEnd(mBookUtil.getPosition());
        return trPage;
    }

    public List<String> getNextLines(){
        List<String> lines = new ArrayList<>();
        float width = 0;
        float height = 0;
        String line = "";
        while (mBookUtil.next(true) != -1){
            char word = (char) mBookUtil.next(false);
            //判断是否换行
            if ((word + "" ).equals("\r") || (((char) mBookUtil.next(true)) + "").equals("\n")){
                mBookUtil.next(false);
                if (!line.isEmpty()){
                    lines.add(line);
                    line = "";
                    width = 0;
                    if (lines.size() == mLineCount){
                        break;
                    }
                }
            }else {
                float widthChar = mPaint.measureText(word + "");
                width += widthChar;
                if (width > mVisibleWidth) {
                    width = widthChar;
                    lines.add(line);
                    line = word + "";
                } else {
                    line += word;
                }
            }

            if (lines.size() == mLineCount){
                if (!line.isEmpty()){
                    mBookUtil.setPosition(mBookUtil.getPosition() - 1);
                }
                break;
            }
        }

        if (!line.isEmpty() && lines.size() < mLineCount){
            lines.add(line);
        }
        if(lines.size() > 0){
            String s = lines.get(lines.size() - 1).trim();

            boolean b = s.length() == 1;

            if(b){
                lines.remove(lines.size() - 1);
            }

        }

        for (String str : lines){
            Log.e(TAG,str + "   ");
        }
        return lines;
    }

    public List<String> getPreLines(){
        List<String> lines = new ArrayList<>();
        float width = 0;
        String line = "";

        char[] par = mBookUtil.preLine();
        while (par != null){
            List<String> preLines = new ArrayList<>();
            for (int i = 0 ; i < par.length ; i++){
                char word = par[i];
                float widthChar = mPaint.measureText(word + "");
                width += widthChar;
                if (width > mVisibleWidth) {
                    width = widthChar;
                    preLines.add(line);
                    line = word + "";
                } else {
                    line += word;
                }
            }
            if (!line.isEmpty()){
                preLines.add(line);
            }

            lines.addAll(0,preLines);

            if (lines.size() >= mLineCount){
                break;
            }
            width = 0;
            line = "";
            par = mBookUtil.preLine();
        }

        List<String> reLines = new ArrayList<>();
        int num = 0;
        for (int i = lines.size() -1;i >= 0;i --){
            if (reLines.size() < mLineCount) {
                reLines.add(0,lines.get(i));
            }else{
                num = num + lines.get(i).length();
            }
        }

        if (num > 0){
            if ( mBookUtil.getPosition() > 0) {
                mBookUtil.setPosition(mBookUtil.getPosition() + num + 2);
            }else{
                mBookUtil.setPosition(mBookUtil.getPosition() + num );
            }
        }

        return reLines;
    }

    //上一章
    public void preChapter(){
        if (mBookUtil.getDirectoryList().size() > 0){
            int num = currentCharter;
            if (num ==0){
                num =getCurrentCharter();
            }
            num --;
            if (num >= 0){
                long begin = mBookUtil.getDirectoryList().get(num).getBookCatalogueStartPos();
                currentPage = getPageForBegin(begin);
                currentPage(true);
                currentCharter = num;
            }
        }
    }

    //下一章
    public void nextChapter(){
        int num = currentCharter;
        if (num == 0){
            num =getCurrentCharter();
        }
        num ++;
        if (num < getDirectoryList().size()){
            long begin = getDirectoryList().get(num).getBookCatalogueStartPos();
            currentPage = getPageForBegin(begin);
            currentPage(true);
            currentCharter = num;
        }
    }

    //获取现在的章
    public int getCurrentCharter(){
        int num = 0;
        for (int i = 0;getDirectoryList().size() > i;i++){
            BookCatalogue bookCatalogue = getDirectoryList().get(i);
            if (currentPage.getEnd() >= bookCatalogue.getBookCatalogueStartPos()){
                num = i;
            }else{
                break;
            }
        }
        return num;
    }

    //绘制当前页面
    public void currentPage(Boolean updateChapter){
        onDraw(mBookPageWidget.getCurPage(),currentPage.getLines(),updateChapter);
        onDraw(mBookPageWidget.getNextPage(),currentPage.getLines(),updateChapter);
    }

    //更新电量
    public void updateBattery(int mLevel){
        if (currentPage != null && mBookPageWidget != null && !mBookPageWidget.isRunning()) {
            if (level != mLevel) {
                level = mLevel;
                currentPage(false);
            }
        }
    }

    public void updateTime(){
        if (currentPage != null && mBookPageWidget != null && !mBookPageWidget.isRunning()) {
            String mDate = sdf.format(new java.util.Date());
            if (date != mDate) {
                date = mDate;
                currentPage(false);
            }
        }
    }

    //改变进度
    public void changeProgress(float progress){
        long begin = (long) (mBookUtil.getBookLen() * progress);
        currentPage = getPageForBegin(begin);
        currentPage(true);
    }

    //改变进度
    public void changeChapter(long begin){
        currentPage = getPageForBegin(begin);
        currentPage(true);
    }

    //改变字体大小
    public void changeFontSize(int fontSize){
        this.m_fontSize = fontSize;
        mPaint.setTextSize(m_fontSize);
        calculateLineCount();
        measureMarginWidth();
        currentPage = getPageForBegin(currentPage.getBegin());
        currentPage(true);
    }

    //改变字体
    public void changeTypeface(Typeface typeface){
        this.typeface = typeface;
        mPaint.setTypeface(typeface);
        mBatteryPaint.setTypeface(typeface);
        calculateLineCount();
        measureMarginWidth();
        currentPage = getPageForBegin(currentPage.getBegin());
        currentPage(true);
    }

    //改变背景
    public void changeBookBg(int type){
        setBookBg(type);
        currentPage(false);
    }

    //设置页面的背景
    public void setBookBg(int type){
        Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        int color = 0;
        switch (type){
            case Config.BOOK_BG_DEFAULT:
                canvas = null;
                bitmap.recycle();
                if (getBgBitmap() != null) {
                    getBgBitmap().recycle();
                }
                bitmap = BitmapUtil.decodeSampledBitmapFromResource(
                        mContext.getResources(), R.drawable.paper, mWidth, mHeight);
                color = mContext.getResources().getColor(R.color.read_font_default);
                setBookPageBg(mContext.getResources().getColor(R.color.read_bg_default));
                break;
            case Config.BOOK_BG_1:
                canvas.drawColor(mContext.getResources().getColor(R.color.read_bg_1));
                color = mContext.getResources().getColor(R.color.read_font_1);
                setBookPageBg(mContext.getResources().getColor(R.color.read_bg_1));
                break;
            case Config.BOOK_BG_2:
                canvas.drawColor(mContext.getResources().getColor(R.color.read_bg_2));
                color = mContext.getResources().getColor(R.color.read_font_2);
                setBookPageBg(mContext.getResources().getColor(R.color.read_bg_2));
                break;
            case Config.BOOK_BG_3:
                canvas.drawColor(mContext.getResources().getColor(R.color.read_bg_3));
                color = mContext.getResources().getColor(R.color.read_font_3);
                if (mBookPageWidget != null) {
                    mBookPageWidget.setBgColor(mContext.getResources().getColor(R.color.read_bg_3));
                }
                break;
            case Config.BOOK_BG_4:
                canvas.drawColor(mContext.getResources().getColor(R.color.read_bg_4));
                color = mContext.getResources().getColor(R.color.read_font_4);
                setBookPageBg(mContext.getResources().getColor(R.color.read_bg_4));
                break;
        }

        setBgBitmap(bitmap);
        setM_textColor(color);
    }

    public void setBookPageBg(int color){
        if (mBookPageWidget != null) {
            mBookPageWidget.setBgColor(color);
        }
    }

    /**
     * 设置日间夜间
     * @param isNight
     */
    public void setDayOrNight(Boolean isNight){
        initBg(isNight);
        currentPage(false);
    }

    /**
     * 清除页面属性
     */
    public void clear(){
        currentCharter = 0;
        bookPath = "";
        bookName = "";
        bookList = null;
        mBookPageWidget = null;
        mPageEvent = null;
        cancelPage = null;
        prePage = null;
        currentPage = null;
    }

    public static Status getStatus(){
        return mStatus;
    }

    public long getBookLen(){
        return mBookUtil.getBookLen();
    }

    public TRPage getCurrentPage(){
        return currentPage;
    }

    /**
     * 获取书本章节
     * @return
     */
    public List<BookCatalogue> getDirectoryList(){
        return mBookUtil.getDirectoryList();
    }

    /**
     *  获取书本路径
     * @return
     */
    public String getBookPath(){
        return bookPath;
    }

    /**
     *  是否第一页
     * @return
     */
    public boolean isFirstPage() {
        return m_isFirstPage;
    }
    /**
     *  是否最后一页
     * @return
     */
    public boolean isLastPage() {
        return m_isLastPage;
    }


    /**
     * 设置页面背景
     * @param BG
     */
    public void setBgBitmap(Bitmap BG) {
        m_book_bg = BG;
    }

    /**
     *  返回页面背景
     * @return
     */
    public Bitmap getBgBitmap() {
        return m_book_bg;
    }


    /**
     * 设置文字颜色
     * @param m_textColor
     */
    public void setM_textColor(int m_textColor) {
        this.m_textColor = m_textColor;
    }

    /**
     * 获取文字颜色
     * @return
     */
    public int getTextColor() {
        return this.m_textColor;
    }

    /**
     *  获取文字大小
     * @return
     */
    public float getFontSize() {
        return this.m_fontSize;
    }


    /**
     *  设置主页面
     * @param mBookPageWidget
     */
    public void setPageWidget(PageWidget mBookPageWidget){
        this.mBookPageWidget = mBookPageWidget;
    }


    /**
     *  设置页面的阅读进度监听
     * @param pageEvent
     */
    public void setPageEvent(PageEvent pageEvent){
        this.mPageEvent = pageEvent;
    }


    /**
     *  页面阅读进度监听接口
     */
    public interface PageEvent{
        void changeProgress(float progress);
    }


    public long getBookLength(){
        return mBookUtil.getBookLen();
    }




    public long getAllNextLines(){
        List<String> lines = new ArrayList<>();
        List<List<String>> pages = new ArrayList<>();
        long lastPagePosition = 0;
        long twoLastPagePosition = 0;
        float width = 0;
        int lastPage = 0;
        String line = "";

        while (mBookUtil.next(true) != -1){
            char word = (char) mBookUtil.next(false);
            if ((word + "" ).equals("\r") || (((char) mBookUtil.next(true)) + "").equals("\n")){
                mBookUtil.next(false);
                if (!line.isEmpty()){
                    lines.add(line);
                    line = "";
                    width = 0;
                }
            }else {
                float widthChar = mPaint.measureText(word + "");
                width += widthChar;
                if (width > mVisibleWidth) {
                    width = widthChar;
                    lines.add(line);
                    line = word + "";
                } else {
                    line += word;
                }
            }



            if(lines.size() == mLineCount){
                pages.add(lines);
                lines.clear();
                twoLastPagePosition = lastPagePosition;
                lastPagePosition = mBookUtil.getPosition();
                lastPage++;
            }

        }

        if (!line.isEmpty() && lines.size() < mLineCount){
            lines.add(line);
        }

        if(lines.size() > 0){
            String s = lines.get(lines.size() - 1).trim();

            boolean b = s.length() == 1;

            if(b){
                lines.remove(lines.size() - 1);
            }

        }

        if(lines.size() == 0){
            lastPagePosition = twoLastPagePosition;
        }
        return lastPagePosition;
    }
}
