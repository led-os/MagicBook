package com.key.magicbook.bean;

import java.util.List;

/**
 * created by key  on 2020/4/17
 */
public class BookRankContentBean {

    private List<BookRankContent> contents;
    private int belongWho;

    public List<BookRankContent> getContents() {
        return contents;
    }

    public void setContents(List<BookRankContent> contents) {
        this.contents = contents;
    }

    public int getBelongWho() {
        return belongWho;
    }

    public void setBelongWho(int belongWho) {
        this.belongWho = belongWho;
    }

    public class BookRankContent{
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
