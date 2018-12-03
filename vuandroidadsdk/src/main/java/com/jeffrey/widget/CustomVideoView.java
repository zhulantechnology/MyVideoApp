package com.jeffrey.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ProviderInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.sax.RootElement;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jeffrey.adutil.Utils;
import com.jeffrey.constant.SDKConstant;
import com.jeffrey.core.AdParameters;
import com.jeffrey.vuandroidadsdk.R;

/**
 * Created by Jun.wang on 2018/9/9.
 */

public class CustomVideoView extends RelativeLayout implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener,
MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, TextureView.SurfaceTextureListener{

    // Constant
    private static final String TAG = "MraidVideoView";
    private static final int TIME_MSG = 0X01;   // 更新播放时间的消息
    private static final int TIME_INVAL = 1000;   // 更新播放时间的消息间隔
    private static final int STATE_ERROR = -1;   // 以下是播放的4中状态
    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING= 1;
    private static final int STATE_PAUSING= 2;
    private static final int LOAD_TOTAL_COUNT= 3;    // load的次数


    // Status 状态保护
    private boolean canPlay = true;
    private boolean mIsRealPause;
    private boolean mIsComplete;
    private int mCurrentCount;
    private int playerState = STATE_IDLE;


    // UI
    private ViewGroup mParentContainer;   // 这个播放器View的父容器
    private RelativeLayout mPlayerView;   // 播放器View的Layout文件，包括播放器和一些Icon
    private TextureView mVideoView;  // 播放视频的View
    private Button mMiniPlayBtn;   // 播放View
    private ImageView mFullBtn;    // 全屏View
    private ImageView mLoadingBar;   // 加载动画
    private ImageView mFrameView;   // 停止播放的View
    private AudioManager audioManager;


    // Data
    private String mUrl;    // 视频地址
    private String mFrameURI;
    private boolean isMute;
    private int mScreenWidth, mDestationHeight;

    private MediaPlayer mediaPlayer;
    private ADVideoPlayerListener listener;   // 视频播放器的监听器，比如全屏，暂停，开始播放，加载完成等
    private ADFrameImageLoadListener mFrameLoadListener;  // 感觉是播放暂停是显示一个广告帧图
    private ScreenEventReceiver mScreenReceiver;  // 响应解锁或灭屏的Intent

    private Surface videoSurface;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    if (isPlaying()) {
                        listener.onBufferUpdate(getCurrentPosition());  //向服务端发送播放进度
                        sendEmptyMessageDelayed(TIME_MSG, TIME_INVAL);
                    }
                    break;
            }
        }
    };

    // 自定义构造方法，传递上下文Context和这个View的父容器
    public CustomVideoView(Context context, ViewGroup parentContainer) {
        super(context);
        mParentContainer = parentContainer;
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();
        registerBroadcastReceiver();
    }

    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mDestationHeight = (int) (mScreenWidth * SDKConstant.VIDEO_HEIGHT_PERCENT);
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.xadsdk_video_player, this);
        mVideoView = mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setSurfaceTextureListener(this);
        initSmallLayoutMode();
    }

    private void initSmallLayoutMode() {
        LayoutParams params = new LayoutParams(mScreenWidth, mDestationHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);

        mMiniPlayBtn = mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mLoadingBar = mPlayerView.findViewById(R.id.loading_bar);
        mFrameView = mPlayerView.findViewById(R.id.framing_view);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
    }
    public void setListener(ADVideoPlayerListener listener) {
        this.listener = listener;
    }


    private void startPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == this.mMiniPlayBtn) {
            if (this.playerState == STATE_PAUSING) {
                if (Utils.getVisiblePercent(mParentContainer)
                        > SDKConstant.VIDEO_SCREEN_PERCENT) {
                    resume();
                    this.listener.onClickPlay();
                }
            } else {
                load();
            }
        } else if (v ==this.mFullBtn) {
            this.listener.onClickFullScreenBtn();
        } else if (v == mVideoView){
            this.listener.onClickVideo();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (listener != null) {
            listener.onAdVideoLoadComplete();
        }
        playBack();
        setIsComplete(true); // 播放完毕进入真正的暂停状态，滑动的时候不会自动播放的
        setIsRealPause(true);
    }

    // 播放完毕后回到初始状态
    public void playBack() {
        setCurrentPlayState(STATE_PAUSING);
        mHandler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
        this.showPauseView(false);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
       // return false; // 系统默认出来

        return true;  // 如果我们自己处理完，return true，系统就不会再处理了。
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    // 播放器处于就绪状态，这个方法不是调用的，而是因为load中实现了mediaPlayer.prepareAsync
    // 才会回调onPrepared方法
    @Override
    public void onPrepared(MediaPlayer mp) {
        startPlayView();
        mediaPlayer = mp;
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(this);
            mCurrentCount = 0;
            if (listener != null) {
                listener.onAdVideoLoadSuccess();
            }
            decideCanPlay();
        }
    }

    private void decideCanPlay() {
        // 满足自动播放条件，则直接播放
        if (Utils.canAutoPlay(getContext(), AdParameters.getCurrentSetting()) &&
                Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT) {
            setCurrentPlayState(STATE_PAUSING);
            resume();
        } else {
            setCurrentPlayState(STATE_PLAYING);
            pause();
        }
    }

    // 暂停视频播放
    public void pause() {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                this.mediaPlayer.seekTo(0);
            }
        }
        this.showPauseView(false);
        mHandler.removeCallbacksAndMessages(null);
    }

    // 恢复视频播放
    public void resume() {
        if (this.playerState != STATE_PAUSING) {
            return;
        }
        if (!isPlaying()) {
            entryResumeState(); //置为播放中的状态值
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.start();
            // 只有在resume的时候开始发送消息，在别的状态要remove掉
            mHandler.sendEmptyMessage(TIME_MSG);
            showPauseView(true);
        } else {
            showPauseView(false);
        }
    }

    private void showPauseView(boolean show) {
        mFullBtn.setVisibility(show ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        if (!show) {
            mFrameView.setVisibility(View.VISIBLE);
            loadFrameImage();
        } else {
            mFrameView.setVisibility(View.GONE);
        }
        
    }

    // 销毁我们的自定义View
    public void destory() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setBackgroundResource(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unRegisterBroadcastReceiver();
        mHandler.removeCallbacksAndMessages(null); //release all message and runnable
        showPauseView(false);   //除了播放和loading外其余任何状态都显示pause
    }


    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    public void setIsComplete(boolean isComplete) {
        this.mIsComplete = isComplete;
    }

    public void setIsRealPause(boolean isRealPause) {
        this.mIsRealPause = isRealPause;
    }

    public void setDataSource(String url) {
        this.mUrl = url;
    }

    public void setFrameURI(String url) {
        this.mFrameURI = url;
    }

    // 进入播放状态时的状态更新
    private void entryResumeState() {
        canPlay = true;
        setCurrentPlayState(STATE_PLAYING);
        setIsRealPause(false);
        setIsComplete(false);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        videoSurface = new Surface(surface);
        checkMediaPlayer();
        mediaPlayer.setSurface(videoSurface);
        load();
    }

    private synchronized void checkMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = createMediaPlayer();
        }
    }

    public void stop() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        setCurrentPlayState(STATE_IDLE);

        if (mCurrentCount < LOAD_TOTAL_COUNT) {
            mCurrentCount += 1;
            load();
        } else {
            showPauseView(false);
        }
    }

    // 根据url加载视频
    public void load() {
        if (this.playerState != STATE_IDLE) {
            return;
        }
        showLoadingView();
        try {
            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer(); //完成播放器的创建工作
            mute(true);
            mediaPlayer.setDataSource(this.mUrl);
            mediaPlayer.prepareAsync();  //开始异步加载
        } catch (Exception e) {
            stop();
        }
    }

    // 设置静音
    public void mute(boolean mute) {
        isMute = mute;
        if (mediaPlayer != null && this.audioManager != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public void setFrameLoadListener(ADFrameImageLoadListener frameLoadListener) {
        this.mFrameLoadListener = frameLoadListener;
    }

    private void showLoadingView() {
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getBackground();
        anim.start();
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
        loadFrameImage();
    }

    private MediaPlayer createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (videoSurface != null && videoSurface.isValid()) {
            mediaPlayer.setSurface(videoSurface);
        } else {
            stop();
        }
        return mediaPlayer;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void setCurrentPlayState(int state) {
        playerState = state;
    }

    // 异步加载定帧图
    private void loadFrameImage() {
        if (mFrameLoadListener != null) {
            mFrameLoadListener.onStartFrameLoad(mFrameURI, new ImageLoaderListener() {
                @Override
                public void onLoadingComplete(Bitmap loadedImage) {
                    if (loadedImage != null) {
                        mFrameView.setScaleType(ScaleType.FIT_XY);
                        mFrameView.setImageBitmap(loadedImage);
                    } else {
                        mFrameView.setScaleType(ScaleType.FIT_CENTER);
                        mFrameView.setImageResource(R.drawable.xadsdk_img_error);
                    }
                }
            });
        }
    }

    // 当前的播放位置
    public int getCurrentPosition() {
        if (this.mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public boolean isRealPause() {
        return mIsRealPause;
    }

    public boolean isComplete() {
        return mIsComplete;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && playerState == STATE_PAUSING) {
            if (isRealPause() && isComplete()) {
                pause();
            } else {
                decideCanPlay();
            }
        } else {
            pause();
        }
    }

    // 销毁我们的自定义View
    public void destroy() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setCurrentPlayState(STATE_IDLE);
        mCurrentCount = 0;
        setIsRealPause(false);
        setIsComplete(false);
        unRegisterBroadcastReceiver();
        mHandler.removeCallbacksAndMessages(null);// release all message and runnable
        showPauseView(false);
    }

    private void registerBroadcastReceiver() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, filter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (mScreenReceiver != null) {
            getContext().unregisterReceiver(mScreenReceiver);
        }
    }
    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_USER_PRESENT:   // 解锁的Intent
                    if (playerState == STATE_PAUSING) {
                        if (isRealPause()) {
                            pause();
                        } else {
                            decideCanPlay();
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:   // 灭屏的Intent
                    if (playerState == STATE_PLAYING){
                        pause();
                    }
                    break;
            }
        }
    }

    // 跳转到指定点播放视频
    public void seekAndResume(int position) {
        if (mediaPlayer != null) {
            showPauseView(true);
            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.start();
                    mHandler.sendEmptyMessage(TIME_MSG);
                }
            });
        }
    }

    // 跳到指定点暂停视频
    public void seekAndPause(int position) {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        showPauseView(false); // false时就显示暂停时的播放器UI
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.seekTo(position);
            // mediaPlayer.pause();  // 这种方式只能适用于部分手机，下面的方式适配性好
            // seekTo的事件监听
            /*
                seekTo 之后的动作需要在完成seekTo后执行，使用下面的回调才能保证
             */
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.pause();
                    mHandler.removeCallbacksAndMessages(null);
                }
            });
            
        }
    }

    //全屏不显示暂停状态,后续可以整合，不必单独出一个方法
    public void pauseForFullScreen() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                mediaPlayer.seekTo(0);
            }
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    /**
     * 供业务层来实现具体的回调逻辑
     */
    public interface ADVideoPlayerListener {
        public void onBufferUpdate(int time);  // 视频播放器播放到了第几秒
        public void onClickFullScreenBtn(); // 跳转到全屏播放
        public void onClickVideo();
        public void onClickBackBtn();
        public void onClickPlay();
        public void onAdVideoLoadSuccess();
        public void onAdVideoLoadFailed();
        public void onAdVideoLoadComplete();
    }

    public interface ImageLoaderListener {
        // 如果图片下载不成功，传null  （应该是我们经常看视频暂停时的广告点击图片）
        void onLoadingComplete(Bitmap loadedImage);
    }

    public interface ADFrameImageLoadListener {
        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }
}







































