package com.jeffrey.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jeffrey.adutil.Utils;
import com.jeffrey.constant.SDKConstant;
import com.jeffrey.core.video.VideoAdSlot;
import com.jeffrey.core.video.VideoAdSlot.AdSDKSlotListener;
import com.jeffrey.module.AdValue;
import com.jeffrey.vuandroidadsdk.R;
import com.jeffrey.widget.CustomVideoView.ADVideoPlayerListener;

/**
 * Created by Jun.wang on 2018/9/27.
 */

public class VideoFullDialog extends Dialog implements ADVideoPlayerListener {
    private static final String TAG = VideoFullDialog.class.getSimpleName();
    private CustomVideoView mVideoView;

    private Context mContext;
    private RelativeLayout mRootView;
    private ViewGroup mParentView;
    private ImageView mBackButton;

    private AdValue mXAdInstance;
    private int mPosition; //从小屏到全屏时视频的播放位置
    private FullToSmallListener mListener;
    private boolean isFirst;

    // 动画要执行的平移值
    private int deltaY;
    private AdSDKSlotListener mSlotListener;
    // 用于Dialog出入场动画所传递的数据
    private Bundle mStartBundle;
    private Bundle mEndBundle;

    public VideoFullDialog(Context context, CustomVideoView videoView, AdValue instance,
                           int position) {
        super(context, R.style.dialog_full_screen); // 通过style的设置，保证我们Dialog的全屏
        mContext = context;
        mXAdInstance = instance;
        mPosition = position;
        mVideoView = videoView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.xadsdk_dialog_video_layout);
        initViedoView();
    }

    private void initViedoView() {
        mParentView = findViewById(R.id.content_layout);
        mBackButton = findViewById(R.id.xadsdk_player_close_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBackBtn();
            }
        });
        mRootView = (RelativeLayout) findViewById(R.id.root_view);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo();
            }
        });
        mRootView.setVisibility(View.INVISIBLE);

        mVideoView.setListener(this); // 设置事件监听为当前的对话框
        mVideoView.mute(false);
        mParentView.addView(mVideoView);
        mParentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mParentView.getViewTreeObserver().removeOnPreDrawListener(this);
                prepareScene();
                runEnterAnimation();
                return true;
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mVideoView.isShowFullBtn(false); //防止第一次有些手机仍显示全屏按钮
        if (!hasFocus) {
            //未取得焦点时的逻辑
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pauseForFullScreen();
        } else {
            // 取得焦点时的逻辑
            if (isFirst) { //为了适配某些手机不执行seekandResume中的播放方法
                mVideoView.seekAndPause(mPosition);
            } else {
                mVideoView.resume();
            }
        }
        isFirst = false;
    }

    // dialog销毁的时候调用
    @Override
    public void dismiss() {
        mParentView.removeView(mVideoView);
        super.dismiss();
    }

    // 准备入场动画
    private void runEnterAnimation() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(0)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mRootView.setVisibility(View.VISIBLE);
                    }
                }).start();
    }

    //准备出场动画
    private void runExitAnimator() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(deltaY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        try {
                            /*ReportManager.exitfullScreenReport(mXAdInstance.event.exitFull.content, mVideoView.getCurrentPosition()
                                    / SDKConstant.MILLION_UNIT);*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mListener != null) {
                            mListener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
                        }
                    }
                }).start();
    }

    // 准备动画所需数据
    private void prepareScene() {
        mEndBundle = Utils.getViewProperty(mVideoView);
        // 将destationView移到originalView位置处
        deltaY = (mStartBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP) -
                    mEndBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP));
        mVideoView.setTranslationY(deltaY);
    }

    public void setViewBundle(Bundle bundle) {
        mStartBundle = bundle;
    }
    public void setListener(FullToSmallListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onBufferUpdate(int time) {
        try {
            if (mXAdInstance != null) {
              //  ReportManager.suReport(mXAdInstance.middleMonitor, time / SDKConstant.MILLION_UNIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickFullScreenBtn() {
        onClickVideo();
    }

    @Override
    public void onClickVideo() {
    /*    String desationUrl = mXAdInstance.clickUrl;
        if (mSlotListener != null) {
            if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                mSlotListener.onClickVideo(desationUrl);
                try {
                    ReportManager.pauseVideoReport(mXAdInstance.clickMonitor, mVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //走默认样式
            if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                Intent intent = new Intent(mContext, AdBrowserActivity.class);
                intent.putExtra(AdBrowserActivity.KEY_URL, mXAdInstance.clickUrl);
                mContext.startActivity(intent);
                try {
                    ReportManager.pauseVideoReport(mXAdInstance.clickMonitor, mVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    @Override
    public void onClickBackBtn() {
        runExitAnimator();
    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    // 不是加载结束，而是播放结束
    @Override
    public void onAdVideoLoadComplete() {
        try{
            int position = mVideoView.getDuration() / SDKConstant.MILLION_UNIT;
           // ReportManager.sueReport(mXAdInstance.endMonitor, true, position);
        }catch (Exception e) {
            e.printStackTrace();
        }
        dismiss();
        if (mListener != null) {
            mListener.playComplete();
        }
    }

    public void setSlotListener(AdSDKSlotListener slotListener) {
        this.mSlotListener = slotListener;
    }

    // 与业务逻辑层（VideoAdSlot) 进行通信
    public interface FullToSmallListener {
        void getCurrentPlayPosition(int position); //全屏播放中点击关闭按钮或back键时回调
        void playComplete();    // 全屏播放结束时回调
    }
}






























