package com.key.magicbook.jsoup;

import androidx.annotation.NonNull;

import com.allen.library.interceptor.Transformer;
import com.key.magicbook.api.ApiHelper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * created by key  on 2020/3/30
 */
public class RxJsoup {

    private static final String TAG = "RXJSOUP";
    private final String url;
    private Document document;


    private boolean exceptionIfNotFound = false;

    public RxJsoup(String url, boolean exceptionIfNotFound) {
        this.url = url;
        this.exceptionIfNotFound = exceptionIfNotFound;
    }

    public static Observable<Connection.Response> connect(final Connection jsoupConnection) {
        return Observable.create(new ObservableOnSubscribe<Connection.Response>() {
            @Override
            public void subscribe(ObservableEmitter<Connection.Response> observableEmitter) throws Exception {
                try {
                    final Connection.Response response = jsoupConnection.execute();
                    observableEmitter.onNext(response);
                    observableEmitter.onComplete();
                } catch (Exception e) {
                    observableEmitter.onError(e);
                }
            }

        });
    }

    public RxJsoup setExceptionIfNotFound(boolean exceptionIfNotFound) {
        this.exceptionIfNotFound = exceptionIfNotFound;
        return this;
    }

    public static RxJsoup with(String url) {
        return new RxJsoup(url, false);
    }

    public Observable<String> attr(final Element element, final String expression, final String attr) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                final Elements elements = element.select(expression);
                if (elements.isEmpty() && exceptionIfNotFound) {
                    observableEmitter.onError(new NotFoundException(expression, element.toString()));
                } else {
                    if (elements.isEmpty()) {
                        observableEmitter.onNext("");
                    } else {
                        for (Element e : elements) {
                            observableEmitter.onNext(e.attr(attr));
                        }
                    }
                    observableEmitter.onComplete();
                }
            }
        });
    }

    public Observable<String> href(Element element, String expression) {
        return attr(element, expression, "href");
    }

    public Observable<String> src(Element element, String expression) {
        return attr(element, expression, "src");
    }

    public Observable<String> text(final Element element, final String expression) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                final Elements elements = element.select(expression);
                if (elements.isEmpty() && exceptionIfNotFound) {
                    observableEmitter.onError(new NotFoundException(expression, element.toString()));
                } else {
                    if (elements.isEmpty()) {
                        observableEmitter.onNext("");
                    } else {
                        for (Element e : elements) {
                            observableEmitter.onNext(e.text());
                        }
                    }
                    observableEmitter.onComplete();
                }
            }


        });
    }


    private Observable<Document> document() {
        if (document != null) {
            return Observable.just(document);
        } else {
            return Observable.create(new ObservableOnSubscribe<Document>() {
                @Override
                public void subscribe(final ObservableEmitter<Document> observableEmitter) throws Exception {
                    ApiHelper
                            .getFreeUrlApi()
                            .freeUrl(url)
                            .compose(Transformer.<ResponseBody>switchSchedulers())
                            .subscribe(
                                    new Observer<ResponseBody>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(ResponseBody value) {
                                            try {
                                                document = Jsoup.parse(value.string());
                                                observableEmitter.onNext(document);
                                                observableEmitter.onComplete();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    }
                            );
                }
            });
        }
    }

    public Observable<Element> select(final String expression) {
        return document().flatMap(
                new Function<Document, ObservableSource<Element>>() {
                    @Override
                    public ObservableSource<Element> apply(@NonNull final Document document) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Element>() {
                            @Override
                            public void subscribe(ObservableEmitter<Element> observableEmitter) {
                                final Elements elements = document.select(expression);
                                if (elements.isEmpty() && exceptionIfNotFound) {
                                    observableEmitter.onError(new NotFoundException(expression, "document"));
                                } else {
                                    for (Element element : elements) {
                                        observableEmitter.onNext(element);
                                    }
                                    observableEmitter.onComplete();
                                }
                            }
                        });
                    }
                });
    }

    public static Observable<Element> select(final Document documentInside,final String expression) {
        return Observable.create(new ObservableOnSubscribe<Element>() {
            @Override
            public void subscribe(ObservableEmitter<Element> observableEmitter) {
                final Elements elements = documentInside.select(expression);
                if (elements.isEmpty()) {
                    observableEmitter.onError(new Exception(expression + "document no found"));
                } else {
                    for (Element element : elements) {
                        observableEmitter.onNext(element);
                    }
                    observableEmitter.onComplete();
                }
            }
        });
    }


    public Observable<Element> getElementsByAttributeValue(final Element element, final String key, final String value) {
        return Observable.create(new ObservableOnSubscribe<Element>() {
            @Override
            public void subscribe(ObservableEmitter<Element> observableEmitter) throws Exception {
                final Elements elements = element.getElementsByAttributeValue(key, value);
                if (elements.isEmpty() && exceptionIfNotFound) {
                    observableEmitter.onError(new NotFoundException(key + " " + value, element.toString()));
                } else {
                    for (Element e : elements) {
                        observableEmitter.onNext(e);
                    }
                    observableEmitter.onComplete();
                }
            }
        });
    }

    private class NotFoundException extends Exception {
        public NotFoundException(String expression, String document) {
            super("`" + expression + "` not found in `" + document + "`");
        }
    }
}
