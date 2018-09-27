package com.jeffrey.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
        super(context, R.style.dialog_full_screen);
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

    @Override
    public void onAdVideoLoadComplete() {

    }
}
