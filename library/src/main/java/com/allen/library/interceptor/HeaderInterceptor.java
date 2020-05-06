package com.allen.library.interceptor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Allen on 2017/5/3.
 * <p>
 *
 * @author Allen
 * 请求拦截器  统一添加请求头使用
 */

public abstract class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Map<String, String> headers = buildHeaders();
        if (headers == null || headers.isEmpty()) {
            Response proceed = null;
            try {
                proceed = chain.proceed(request);
            }catch (Exception e){
                throw new IOException(e);
            }
            return proceed;
        } else {
            return chain.proceed(request.newBuilder()
                    .headers(buildHeaders(request, headers))
                    .build());
        }
    }

    private Headers buildHeaders(Request request, Map<String, String> headerMap) {
        Headers headers = request.headers();
        if (headers != null) {
            Headers.Builder builder = headers.newBuilder();
            for (String key : headerMap.keySet()) {
                builder.add(key, headerMap.get(key));
            }
            return builder.build();
        } else {
            return null;
        }
    }

    public abstract Map<String, String> buildHeaders();
}
