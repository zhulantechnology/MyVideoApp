package com.jeffrey.network.http;

import com.jeffrey.module.recommand.BaseRecommandModel;
import com.jeffrey.okhttp.CommonOkHttpClient;
import com.jeffrey.okhttp.listener.DisposeDataHandle;
import com.jeffrey.okhttp.listener.DisposeDataListener;
import com.jeffrey.okhttp.request.CommonRequest;
import com.jeffrey.okhttp.request.RequestParams;

/**
 * @function 存放应用中所有的请求
 * Created by Jun.wang on 2018/6/4.
 */

public class RequestCenter {

    //根据参数发送所有post请求
    public static void postRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.
                createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    private static String testUrl = "http://com.jeffrey.test:8899";
    /**
     * 真正的发送首页应用请求
     * BaseRecommandModel.class ----具体要转为的实体对象？？
     * @param listener
     */
    public static void requestRecommandData(DisposeDataListener listener) {
        RequestCenter.postRequest(HttpConstants.HOME_RECOMMAND, null,
                listener, BaseRecommandModel.class);

       // RequestCenter.postRequest(testUrl, null, listener, BaseRecommandModel.class);
    }
}