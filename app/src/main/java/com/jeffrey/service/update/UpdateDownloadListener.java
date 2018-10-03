package com.jeffrey.service.update;

/**
 * Created by Jun.wang on 2018/10/1.
 */

// 定义下载过程的监听器
public interface UpdateDownloadListener {
    // 下载请求开始回调
    public void onStarted();

    /**
     *  请求成功，下载前的准备回调
     * @param contentLength   ---文件长度
     * @param downloadUrl     ---下载地址
     */
    public void onPrepared(long contentLength, String downloadUrl);

    // 进度更新回调
    public void onProgressChanged(int progress, String downloadUrl);

    // 下载过程中暂停的回调
    public void onPaused(int progress, int completeSize, String downloadUrl);

    // 下载完成回调
    public void onFinished(int completeSize, String downloadUrl);

    // 下载失败回调
    public void onFailure();
}






























