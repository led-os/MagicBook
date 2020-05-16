package com.key.magicbook.document;

import com.key.magicbook.db.BookDetail;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;

/**
 * created by key  on 2020/5/11
 */
public abstract class ParseDocument {

    public abstract BookDetail parseBookDetail(Document document);
    public abstract String parseBookCover(Document document);
    public abstract String parseBookContent(Document document);

    public abstract List<BookDetail> parsePile(Document document);
    public abstract List<BookDetail> parseTypeOne(Document document);
    public abstract List<BookDetail> parseTypeTwo(Document document);
    public abstract List<BookDetail> parseTypeThree(Document document);
    public abstract List<BookDetail> parseTypeFour(Document document);
    public abstract List<BookDetail> parseTypeFive(Document document);
    public abstract List<BookDetail> parseTypeSix(Document document);
    public abstract List<BookDetail> parseTypeSeven(Document document);
    public abstract List<BookDetail> parseTypeEight(Document document);



    public abstract List<String> getTypeNames();
    public abstract List<String> getTypeUrls();



    public abstract List<BookDetail> parseBookDetails(Document document);
    public abstract List<BookDetail> parseBookHeaders(Document document);



    public abstract List<HashMap<String,String>> parseRankUrls(Document document);

}
