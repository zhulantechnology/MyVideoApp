package com.jeffrey.core;

/**
 * Created by Jun.wang on 2018/9/11.
 */
// 最终通知应用层视频播放是否成功
public interface AdContextInterface {

    void onAdSuccess();

    void onAdFailed();

    void onClickVideo(String url);
}
