package com.jeffrey.core.video;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jeffrey.module.AdValue;
import com.jeffrey.widget.CustomVideoView;
import com.jeffrey.widget.CustomVideoView.ADFrameImageLoadListener;
import com.jeffrey.widget.CustomVideoView.ADVideoPlayerListener;

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

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickFullScreenBtn() {

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

    }

    public interface AdSDKSlotListener {
        public ViewGroup getAdParent();
        public void onAdVideoLoadSuccess();
        public void onAdVideoLoadFailed();
        public void onAdVideoLoadComplete();
        public void onClickVideo(String url);
    }
}

























