package com.key.magicbook.db;

import org.litepal.crud.LitePalSupport;

/**
 * created by key  on 2020/5/10
 */
public class BookReadChapter extends LitePalSupport {
    private String bookChapterOnlyTag;
    private long begin;
    private String chapterName;
    private String chapterUrl;
    private String bookChapterContent;
    private String isLook;
    private String isCache;
    private int chapterNum;


    public int getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getIsCache() {
        return isCache;
    }

    public void setIsCache(String isCache) {
        this.isCache = isCache;
    }

    public String getBookChapterContent() {
        return bookChapterContent;
    }

    public void setBookChapterContent(String bookChapterContent) {
        this.bookChapterContent = bookChapterContent;
    }

    public String getBookChapterOnlyTag() {
        return bookChapterOnlyTag;
    }

    public void setBookChapterOnlyTag(String bookChapterOnlyTag) {
        this.bookChapterOnlyTag = bookChapterOnlyTag;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getIsLook() {
        return isLook;
    }

    public void setIsLook(String isLook) {
        this.isLook = isLook;
    }
}
