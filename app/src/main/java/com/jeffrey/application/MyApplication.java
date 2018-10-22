package com.jeffrey.application;

import android.app.Application;

import com.jeffrey.core.AdSDKManager;
import com.jeffrey.share.ShareManager;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Jun.wang on 2018/5/27.
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
