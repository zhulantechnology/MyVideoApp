package com.jeffrey.service.update;

import android.widget.ThemedSpinnerAdapter;

import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 下载调度器管理
 * Created by Jun.wang on 2018/10/2.
 */

public class UpdateManager {
    private static UpdateManager manager;
    private ThreadPoolExecutor threadPool;
    private UpdateDownloadRequest downloadRequest;

    private UpdateManager() {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    static {
        manager = new UpdateManager();
    }

    public static UpdateManager getInstance() {
        return manager;
    }

    public void startDownload(String downloadUrl, String localFilePath,
                              UpdateDownloadListener downloadListener) {
        if (downloadRequest != null && downloadRequest.isDownloading()) {
            return;
        }
        downloadRequest = new UpdateDownloadRequest(downloadUrl, localFilePath,
                            downloadListener);
        Future<?> request = threadPool.submit(downloadRequest);
        new WeakReference<Future<?>>(request);
    }

    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}




























