package com.key.magicbook.jsoup;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.key.keylibrary.utils.FileUtils;
import com.key.magicbook.bean.Area;
import com.key.magicbook.util.CommonUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * created by key  on 2020/2/25
 */
public class JsoupUtils {




//    Thread(Runnable {
//        val webClient = WebClient()
//        webClient.options.isJavaScriptEnabled = true
//        webClient.options.isThrowExceptionOnScriptError = false
//        webClient.options.isThrowExceptionOnFailingStatusCode = false
//        webClient.options.isCssEnabled = false
//        webClient.ajaxController = NicelyResynchronizingAjaxController()
//        val page: HtmlPage = webClient.getPage("http://www.baidu.com")
//        val formByName = page.getFormByName("f")
//        Thread.sleep(1000)
//        val inputByName = formByName.getInputByName<HtmlTextInput>("wd")
//        inputByName.valueAttribute = "Android"
//        Thread.sleep(1000)
//
//
//        val inputByValue = formByName.getInputByValue<HtmlSubmitInput>("百度一下")
//        val click = inputByValue.click<HtmlPage>()
//        Thread.sleep(3000)
//
//        Thread.sleep(1000)
//    }).start()

}
