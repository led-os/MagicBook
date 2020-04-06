package com.key.magicbook.api;

import com.allen.library.RxHttpUtils;
import com.key.magicbook.url.FreeUrl;
import com.key.magicbook.url.QiDianUrl;

/**
 * created by key  on 2020/3/30
 */
public class ApiHelper {

    public static QIDIANApi getQIDIANApi() {
        return RxHttpUtils.createApi(QiDianUrl.INSTANCE.getQIDIAN_KEY(), QiDianUrl.INSTANCE.getQIDIAN_URL(), QIDIANApi.class);
    }

    public static FreeUrlApi getFreeUrlApi(){
        return RxHttpUtils.createApi(FreeUrl.INSTANCE.getFREE_KEY(), FreeUrl.INSTANCE.getFREE_URL(), FreeUrlApi.class);
    }
    public static FreeUrlApi getFreeSecondUrlApi(){
        return RxHttpUtils.createApi(FreeUrl.INSTANCE.getDINGDIANN_KEY(), FreeUrl.INSTANCE.getDINGDIANN_URL(), FreeUrlApi.class);
    }
}
