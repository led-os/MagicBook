package com.key.magicbook.bean;

import org.litepal.crud.LitePalSupport;

/**
 * created by key  on 2020/4/3
 */
public class BookSearchHistory extends LitePalSupport {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
