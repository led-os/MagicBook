package com.key.magicbook.db;

import org.jsoup.nodes.Element;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * created by key  on 2020/4/13
 */
public class BookDetail extends LitePalSupport {
    private String bookName;
    private String bookAuthor;
    private String bookBackgroundUrl;
    private String bookIntro;
    private String lastChapter;
    private String lastUpdateTime;
    private String bookCover;
    private String bookUrl;
    private String bookType;
    private List<String> chapterUrls;
    private List<String> chapterNames;
    private List<Element> chapterElements;

    private String isLike;
    private String isBookCase;
    private String baseUrl;
    private String isLooked;

    public String getIsLooked() {
        return isLooked;
    }

    public void setIsLooked(String isLooked) {
        this.isLooked = isLooked;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public String getIsBookCase() {
        return isBookCase;
    }

    public void setIsBookCase(String isBookCase) {
        this.isBookCase = isBookCase;
    }

    public List<Element> getChapterElements() {
        return chapterElements;
    }

    public void setChapterElements(List<Element> chapterElements) {
        this.chapterElements = chapterElements;
    }

    private boolean isLoad = false;
    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
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


    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }


    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }
}
