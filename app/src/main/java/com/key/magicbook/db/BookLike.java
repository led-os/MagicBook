package com.key.magicbook.db;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * created by key  on 2020/5/7
 */
public class BookLike  extends LitePalSupport {


    private String bookName;
    private String bookAuthor;
    private String bookBackgroundUrl;
    private String bookIntro;
    private String lastChapter;
    private String lastUpdateTime;
    private String bookCover;
    private String bookUrl;
    private String bookType;
    private String bookOnlyTag;
    private List<String> chapterUrls;
    private List<String> chapterNames;
    private String isLike;


    public String getBookOnlyTag() {
        return bookOnlyTag;
    }

    public void setBookOnlyTag(String bookOnlyTag) {
        this.bookOnlyTag = bookOnlyTag;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }



    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookBackgroundUrl() {
        return bookBackgroundUrl;
    }

    public void setBookBackgroundUrl(String bookBackgroundUrl) {
        this.bookBackgroundUrl = bookBackgroundUrl;
    }

    public String getBookIntro() {
        return bookIntro;
    }

    public void setBookIntro(String bookIntro) {
        this.bookIntro = bookIntro;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public List<String> getChapterUrls() {
        return chapterUrls;
    }

    public void setChapterUrls(List<String> chapterUrls) {
        this.chapterUrls = chapterUrls;
    }

    public List<String> getChapterNames() {
        return chapterNames;
    }

    public void setChapterNames(List<String> chapterNames) {
        this.chapterNames = chapterNames;
    }
}
