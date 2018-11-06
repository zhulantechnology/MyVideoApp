package com.jeffrey.application;

import android.app.Application;

import com.jeffrey.core.AdSDKManager;
import com.jeffrey.share.ShareManager;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Jun.wang on 2018/5/27.
 * 1. 整个程序的入口
 * 2. 做很多初始化工作
 * 3. 为整个应用的其它模块提供一个上下文环境
 */

public class MyApplication extends Application {
    private static MyApplication mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initAdSDK();
        initJPush();
        initShareSDK();
    }

    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    private void initAdSDK() {
        AdSDKManager.init(this);
    }

    private void initShareSDK() {
        ShareManager.initSDK(this);
    }

    public static MyApplication getInstance() {
        return mApplication;
    }
}
