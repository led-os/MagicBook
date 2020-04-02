package com.key.magicbook.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * created by key  on 2020/3/30
 */
public interface QIDIANApi {

    @GET("/")
    Observable<ResponseBody> getQIDIANIndex();

}
