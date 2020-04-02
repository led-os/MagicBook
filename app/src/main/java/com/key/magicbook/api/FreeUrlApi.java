package com.key.magicbook.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * created by key  on 2020/3/30
 */
public interface FreeUrlApi {

    @GET
    Observable<ResponseBody> freeUrl(@Url String fileUrl);
}
