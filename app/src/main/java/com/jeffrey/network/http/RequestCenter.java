package com.jeffrey.network.http;

import com.jeffrey.module.recommand.BaseRecommandModel;
import com.jeffrey.module.user.User;
import com.jeffrey.okhttp.CommonOkHttpClient;
import com.jeffrey.okhttp.listener.DisposeDataHandle;
import com.jeffrey.okhttp.listener.DisposeDataListener;
import com.jeffrey.okhttp.request.CommonRequest;
import com.jeffrey.okhttp.request.RequestParams;
import com.jeffrey.update.UpdateModel;

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

    /**
     * 真正的发送首页应用请求
     * BaseRecommandModel.class ----具体要转为的实体对象的类
     * @param listener
     */
    public static void requestRecommandData(DisposeDataListener listener) {
        RequestCenter.postRequest(HttpConstants.HOME_RECOMMAND, null,
                listener, BaseRecommandModel.class);
    }

    //应用版本号请求
    public static void checkVersion(DisposeDataListener listener) {
        RequestCenter.postRequest(HttpConstants.CHECK_UPDATE, null, listener, UpdateModel.class);
    }

    // 用户登录请求
    public static void login(String userName, String passwd, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("mb", userName);
        params.put("pwd", passwd);
        RequestCenter.postRequest(HttpConstants.LOGIN, params, listener, User.class);
    }

}

















