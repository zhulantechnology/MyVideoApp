package com.jeffrey.core;


import com.jeffrey.constant.SDKConstant.AutoPlaySetting;

/**
 * Created by Jun.wang on 2018/9/10.
 */

// 广告SDK全局参数配置，都用静态保持一致性
public class AdParameters {
    // 用来记录可自动播放的条件
     private static AutoPlaySetting currentSetting = AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI;

    public static void setCurrentSetting(AutoPlaySetting setting) {
        currentSetting = setting;
    }

    public static AutoPlaySetting getCurrentSetting() {
        return currentSetting;
    }

    /**
     * 获取sdk当前版本号
     */
    public static String getAdSDKVersion() {
        return "1.0.0";
    }

}




















