package com.key.magicbook.bean;

import java.util.List;

/**
 * created by key  on 2020/4/16
 */
public class BookRankBean {

    private List<BookRank> ranks;

    public List<BookRank> getRanks() {
        return ranks;
    }

    public void setRanks(List<BookRank> ranks) {
        this.ranks = ranks;
    }

    public class BookRank{
        private String name;
        private String url;
        private boolean isChoose = false;

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

        public boolean isChoose() {
            return isChoose;
        }

        public void setChoose(boolean choose) {
            isChoose = choose;
        }
    }
}
