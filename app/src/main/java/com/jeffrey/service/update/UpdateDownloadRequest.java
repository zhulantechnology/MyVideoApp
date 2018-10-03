package com.jeffrey.service.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * 真正执行下载任务的Runnable
 * Created by Jun.wang on 2018/10/2.
 */

    public class UpdateDownloadRequest implements Runnable {
    private int startPos = 0;
    private String downloadUrl;
    private String localFilePath;
    private UpdateDownloadListener downloadListener;
    private DownloadResponseHandler downloadHandler;
    private boolean isDownloading = false;
    private long contentLength;

    /**
     * 构造方法
     * @param downloadUrl  -- 下载的地址
     * @param localFilePath     -- 存储下载文件的路径
     * @param downloadListener      --下载过程中的监听器
     */
    public UpdateDownloadRequest(String downloadUrl, String localFilePath,
                                    UpdateDownloadListener downloadListener) {
        this.downloadUrl = downloadUrl;
        this.localFilePath = localFilePath;
        this.downloadListener = downloadListener;
        downloadHandler = new DownloadResponseHandler();
        isDownloading = true;
    }
    @Override
    public void run() {
        try {
            makeRequest();
        } catch (IOException e) {
            if (downloadHandler != null) {
                downloadHandler.sendFailureMessage(FailureCode.IO);
            }
        } catch (InterruptedException e) {
                    if (downloadHandler != null) {
                        downloadHandler.sendFailureMessage(FailureCode.Interrupted);
                    }
        }

    }

    private void makeRequest() throws IOException, InterruptedException {
        if (!Thread.currentThread().isInterrupted()) {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.connect();
                contentLength = connection.getContentLength();
                if (!Thread.currentThread().isInterrupted()) {
                    if (downloadHandler != null) {
                        // 取得与远程文件的流
                        downloadHandler.sendResponseMessage(connection.getInputStream());
                    }
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    throw e;
                }
            }
        }
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void stopDownloading() {
        isDownloading = false;
    }

    /***********************************************************
     * 下载消息转发器
     */
    public class DownloadResponseHandler {
        protected static final int SUCCESS_MESSAGE = 0;
        protected static final int FAILURE_MESSAGE = 1;
        protected static final int START_MESSAGE = 2;
        protected static final int FINISH_MESSAGE = 3;
        protected static final int NETWORK_OFF = 4;
        private Handler handler;

        private int mCompleteSzie = 0;
        private int progress = 0;
        private static final int PROGRESS_CHANGED = 5;
        private static final int PAUSED_MESSAGE = 7;

        // 构造方法，初始化Handler
        public DownloadResponseHandler() {
            if (Looper.myLooper() != null) {
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        handleSelfMessage(msg);
                    }
                };
            }
        }

        public void onFinish() {
            downloadListener.onFinished(mCompleteSzie, "");
        }

        public void onFailure(FailureCode failureCode) {
            downloadListener.onFailure();
        }

        protected void handleProgressChangedMessage(int progress) {
            downloadListener.onProgressChanged(progress, "");
        }

        protected void handlePausedMessage() {
            downloadListener.onPaused(progress, mCompleteSzie, "");
        }

        private void sendPausedMessage() {
            sendMessage(obtainMessage(PAUSED_MESSAGE, null));
        }

        protected void handleFailureMessage(FailureCode failureCode) {
            onFailure(failureCode);
        }

        protected void sendFailureMessage(FailureCode failureCode) {
            sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { failureCode }));
        }

        private void sendProgressChangedMessage(int progress) {
            sendMessage(obtainMessage(PROGRESS_CHANGED, new Object[] {progress}));
        }

        protected void sendFinishMessage() {
            sendMessage(obtainMessage(FINISH_MESSAGE, null));
        }

        protected void sendMessage(Message msg) {
            if (handler != null) {
                handler.sendMessage(msg);
            } else {
                handleSelfMessage(msg);
            }
        }

        protected Message obtainMessage(int responseMessage, Object response) {
            Message msg = null;
            if (handler != null) {
                msg = this.handler.obtainMessage(responseMessage, response);
            } else {
                msg = Message.obtain();
                msg.what = responseMessage;
                msg.obj = response;
            }
            return msg;
        }

        // Methods which emulate android's Handler and Message method
        protected void handleSelfMessage(Message msg) {
            Object[] response;
            switch (msg.what) {
                case FAILURE_MESSAGE:
                    response = (Object[])msg.obj;
                    handleFailureMessage((FailureCode) response[0]);
                    break;
                case PROGRESS_CHANGED:
                    response = (Object[]) msg.obj;
                    handleProgressChangedMessage(((Integer) response[0]).intValue());
                    break;
                case PAUSED_MESSAGE:
                    handlePausedMessage();
                    break;
                case FINISH_MESSAGE:
                    onFinish();
                    break;
            }
        }

        // 小数点格式化，取小数点后面两位
        private String getTwoPointFloatStr(float value) {
            DecimalFormat fnum = new DecimalFormat("0.00");
            return fnum.format(value);
        }
        void sendResponseMessage(InputStream is) {
            // 随机存取方式
            RandomAccessFile randomAccessFile = null;
            mCompleteSzie = 0;
            try {
                byte[] buffer = new byte[1024];
                int length = -1;
                int limit = 0;
                randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
                randomAccessFile.seek(startPos);
                boolean isPaused = false;
                while ((length = is.read(buffer)) != -1) {
                    if (isDownloading) {
                        randomAccessFile.write(buffer, 0, length);
                        mCompleteSzie += length;
                        if ((startPos + mCompleteSzie) < (contentLength + startPos)) {
                            progress = (int) (Float.parseFloat(getTwoPointFloatStr(
                                    (float)(startPos + mCompleteSzie) /(contentLength + startPos)
                            )) * 100);
                            if ((limit % 30 ==0) || progress == 100) {
                                // 在子线程中读取流数据，后转发到主线程中去
                                sendProgressChangedMessage(progress);
                            }
                        }
                        limit++;
                    } else {
                        isPaused = true;
                        sendPausedMessage();
                        break;
                    }
                }
                stopDownloading();
                if (!isPaused) {
                    sendFinishMessage();
                }
            } catch (IOException e) {
                sendPausedMessage();
                stopDownloading();
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    stopDownloading();
                    e.printStackTrace();
                }

            }
        }

    }

    public enum FailureCode {
        UnknownHost,
        Socket,
        SocketTimeout,
        ConnectTimeout,
        IO,
        HttpResponse,
        Json,
        Interrupted
    }
}




























