package com.jeffrey.application;

import android.app.Application;

/**
 * Created by Jun.wang on 2018/5/27.
 */

public class MyApplication extends Application {
    private static MyApplication mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static MyApplication getInstance() {
        return mApplication;
    }
}
