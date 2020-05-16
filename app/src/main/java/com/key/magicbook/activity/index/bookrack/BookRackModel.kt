package com.key.magicbook.activity.index.bookrack
import com.key.magicbook.jsoup.JsoupUtils
import io.reactivex.Observable
import org.jsoup.nodes.Document

/**
 * created by key  on 2020/5/10
 */
class BookRackModel  : BookRackContract.OnModel {
    override fun parseBookRank(url: String) :Observable<Document> {
      return JsoupUtils.getFreeDocument(url)
    }

    override fun parseBookRankContent(url: String) {

    }


}