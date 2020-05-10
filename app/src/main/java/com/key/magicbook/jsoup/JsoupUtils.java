package com.key.magicbook.jsoup;

import com.allen.library.interceptor.Transformer;
import com.key.magicbook.api.ApiHelper;
import com.key.magicbook.base.ConstantValues;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * created by key  on 2020/2/25
 */
public class JsoupUtils {

    public static String  searchBaseUrl = ConstantValues.BASE_URL;


    public static   Observable<Document> getQIDIANIndex(){
      return ApiHelper.getQIDIANApi().getQIDIANIndex()
              .compose(Transformer.<ResponseBody>switchSchedulers())
              .flatMap(new Function<ResponseBody, ObservableSource<Document>>() {
            @Override
            public ObservableSource<Document> apply(ResponseBody s) throws Exception {
                Document parse = Jsoup.parse(s.string());
                return Observable.create(new ObservableOnSubscribe<Document>() {
                    @Override
                    public void subscribe(ObservableEmitter<Document> e) throws Exception {
                        e.onNext(parse);
                        e.onComplete();
                    }
                });
            }
        });
    }


    public static   Observable<Document> getDingDianSearch(String kw){
        return ApiHelper.getFreeUrlApi().freeUrl(ConstantValues.BASE_URL +"searchbook.php?keyword="+kw)
                .compose(Transformer.<ResponseBody>switchSchedulers())
                .flatMap((Function<ResponseBody, ObservableSource<Document>>) s -> {
                    Document parse = Jsoup.parse(s.string());
                    return Observable.create((ObservableOnSubscribe<Document>) e -> {
                        e.onNext(parse);
                        e.onComplete();
                    });
                });
    }


    public static Observable<Document> getFreeDocument(String url){
        try {
            return ApiHelper.getFreeUrlApi().freeUrl(url)
                    .compose(Transformer.<ResponseBody>switchSchedulers())
                    .flatMap((Function<ResponseBody, ObservableSource<Document>>) s -> {
                        Document parse = Jsoup.parse(s.string());
                        return Observable.create((ObservableOnSubscribe<Document>) e -> {
                            e.onNext(parse);
                            e.onComplete();
                        });
                    });
        }catch (Exception e){
            return null;
        }


    }

    public static Observable<ResponseBody> getFreeDocumentForBody(String url) throws Exception{
        try {
            return ApiHelper.getFreeUrlApi().freeUrl(url)
                    .compose(Transformer.<ResponseBody>switchSchedulers());
        }catch (Exception e){
            return null;
        }


    }




    public static Observable<Element> connectFreeUrl(String url,String expression){
      return RxJsoup.with(url).select(expression);
    }

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
