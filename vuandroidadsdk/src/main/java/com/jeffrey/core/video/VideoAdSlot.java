package com.jeffrey.core.video;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jeffrey.adutil.Utils;
import com.jeffrey.constant.SDKConstant;
import com.jeffrey.core.AdParameters;
import com.jeffrey.module.AdValue;
import com.jeffrey.widget.CustomVideoView;
import com.jeffrey.widget.CustomVideoView.ADFrameImageLoadListener;
import com.jeffrey.widget.CustomVideoView.ADVideoPlayerListener;
import com.jeffrey.widget.VideoFullDialog;
import com.jeffrey.widget.VideoFullDialog.FullToSmallListener;

/**
 * Created by Jun.wang on 2018/9/11.
 */

// 视频业务逻辑层
public class VideoAdSlot implements ADVideoPlayerListener {
    private Context mContext;
    //UI
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;
    // Data
    private AdValue mXAdInstance;  // 将json数据转换成的实体类对象
    private AdSDKSlotListener mSlotListener;  // 传递消息到app context层
    private boolean canPause = false;   // 是否可自动暂停
    private int lastArea = 0;   // 防止将要滑入滑出时播放器的状态改变

    private FullToSmallListener mListener;

    public VideoAdSlot(AdValue adInstance, AdSDKSlotListener slotListener,
                       ADFrameImageLoadListener frameLoadListener) {
        mXAdInstance = adInstance;
        mSlotListener = slotListener;
        mParentView = slotListener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView(frameLoadListener);
    }

    private void initVideoView(ADFrameImageLoadListener frameImageLoadListener) {
        mVideoView = new CustomVideoView(mContext, mParentView);
        if (mXAdInstance != null) {
            mVideoView.setDataSource(mXAdInstance.resource);
            mVideoView.setFrameURI(mXAdInstance.thumb);
            mVideoView.setFrameLoadListener(frameImageLoadListener);
            mVideoView.setListener(this);
        }

        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        mParentView.addView(mVideoView);
    }

    //pause the  video
    private void pauseVideo(boolean isAuto) {
        if (mVideoView != null) {
            if (isAuto) {
                //发自动暂停监测
/*                if (!isRealPause() && isPlaying()) {
                    try {
                        ReportManager.pauseVideoReport(mXAdInstance.event.pause.content, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
            }
            mVideoView.seekAndPause(0);
        }
    }

    //这个滑动的更新只是提供一个功能，其实这里不能自动检测，是需要放在listview或者scrollView中才可以
    public void updateAdInScrollView() {
        int currentArea = Utils.getVisiblePercent(mParentView);
        //小于0表示未出现在屏幕上，不做任何处理
        if (currentArea <= 0) {
            return;
        }
        //刚要滑入和滑出时，异常状态的处理
        // Listview中包含这个View的Item在划入或划出的时候会有这样的异常变化
        if (Math.abs(currentArea - lastArea) >= 100) {
            return;
        }
        if (currentArea < SDKConstant.VIDEO_SCREEN_PERCENT) {
            //进入自动暂停状态，用这个变量控制，如果视频已经在pause状态了，就不需要再pause了
            if (canPause) {
                pauseVideo(true);
                canPause = false; // 滑动事件过滤
            }
            lastArea = 0;
            // 不是真正的暂停，不是真正的播放完成
            mVideoView.setIsComplete(false); // 滑动出50%后标记为从头开始播
            mVideoView.setIsRealPause(false); //以前叫setPauseButtonClick()
            return;
        }

        if (isRealPause() || isComplete()) {
            //进入手动暂停或者播放结束，播放结束和不满足自动播放条件都作为手动暂停
            pauseVideo(false);
            canPause = false;
            return;
        }

        //满足自动播放条件或者用户主动点击播放，开始播放
        if (Utils.canAutoPlay(mContext, AdParameters.getCurrentSetting())
                || isPlaying()) {
            lastArea = currentArea;
            resumeVideo();
            canPause = true;
            mVideoView.setIsRealPause(false);
        } else {
            pauseVideo(false);
            mVideoView.setIsRealPause(true); //不能自动播放则设置为手动暂停效果
        }
    }

    //resume the video
    private void resumeVideo() {
        if (mVideoView != null) {
            mVideoView.resume();
            if (isPlaying()) {
            //    sendSUSReport(true); //发自动播放监测
            }
        }
    }

    private boolean isPlaying() {
        if (mVideoView != null) {
            return mVideoView.isPlaying();
        }
        return false;
    }



    private boolean isRealPause() {
        if (mVideoView != null) {
            return mVideoView.isRealPause();
        }
        return false;
    }

    private boolean isComplete() {
        if (mVideoView != null) {
            return mVideoView.isComplete();
        }
        return false;
    }
    private void bigPlayComplete() {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndPause(0);
        canPause = false;
    }

    @Override
    public void onBufferUpdate(int time) {
        try {
            // 向服务端发送报告视频播放到了第几秒
           // ReportManager.suReport(mXAdInstance.middleMonitor, time / SDKConstant.MILLION_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 实现play层接口，从小屏到全屏播放功能
    @Override
    public void onClickFullScreenBtn() {
        try {
          //  ReportManager.fullScreenReport(mXAdInstance.event.full.content, getPosition());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取videoView在当前界面的属性
        Bundle bundle = Utils.getViewProperty(mParentView);
        // 将播放器从view树种移除
        mParentView.removeView(mVideoView);
        // 创建全屏播放dialog
        VideoFullDialog dialog = new VideoFullDialog(mContext, mVideoView, mXAdInstance,
                                    mVideoView.getCurrentPosition());
        dialog.setListener(new FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                // 在全屏视频播放时点击了返回
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                // 全屏播放完的事件回调
                bigPlayComplete();
            }
        });
        dialog.setViewBundle(bundle);
        dialog.setSlotListener(mSlotListener);
        dialog.show();
    }

    private void backToSmallMode(int position) {
        // 重新添加到View树中
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); // 防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true); // 显示全屏按钮
        mVideoView.mute(true);  // 小屏静音
        mVideoView.setListener(this);   // 重新设置监听为我们的业务逻辑层
        mVideoView.seekAndResume(position); // 使播放器跳到指定位置并播放
        canPause = true;    // 标为可自动暂停
    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {

    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    public void destory() {
        mVideoView.destory();
        mVideoView = null;
        mContext = null;
        mXAdInstance = null;
    }

    @Override
    public void onAdVideoLoadComplete() {
        try {
         //   ReportManager.sueReport(mXAdInstance.endMonitor, false, getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadComplete();
        }
        mVideoView.setIsRealPause(true);

    }

    public interface AdSDKSlotListener {
        public ViewGroup getAdParent();
        public void onAdVideoLoadSuccess();
        public void onAdVideoLoadFailed();
        public void onAdVideoLoadComplete();
        public void onClickVideo(String url);
    }
}

























