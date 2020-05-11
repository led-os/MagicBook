package com.key.magicbook.activity.index.booksecond

import com.key.magicbook.base.ConstantValues
import com.key.magicbook.document.ParseDocumentCreator


/**
 * created by key  on 2020/5/10
 */
class BookSecondModel  : BookSecondContract.OnModel {
    override fun getBookUrls(): List<String> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).typeUrls
    }

    override fun getBookUrlsName(): List<String> {
        return ParseDocumentCreator.getParseDocument(ConstantValues.BASE_URL).typeNames
    }
}