package com.jeffrey.core.video;

import android.view.ViewGroup;

import com.jeffrey.adutil.ResponseEntityToModule;
import com.jeffrey.core.AdContextInterface;
import com.jeffrey.core.video.VideoAdSlot.AdSDKSlotListener;
import com.jeffrey.module.AdValue;
import com.jeffrey.widget.CustomVideoView.ADFrameImageLoadListener;

/**
 * Created by Jun.wang on 2018/9/11.
 */

public class VideoAdContext implements AdSDKSlotListener {
    private ViewGroup mParentView;
    private VideoAdSlot mAdSlot;
    private AdValue mInstance = null;
    private AdContextInterface mListener;

    private ADFrameImageLoadListener mFrameLoadListener;

    public VideoAdContext(ViewGroup parentView, String instance,
                          ADFrameImageLoadListener frameLoadListener) {
        this.mParentView = parentView;
        this.mInstance = (AdValue) ResponseEntityToModule.parseJsonToModule(instance, AdValue.class);
        this.mFrameLoadListener = frameLoadListener;
        load();
    }

    // init the ad 不调用则不会创建videoView
    public void load() {
        if (mInstance != null && mInstance.resource != null) {
            mAdSlot = new VideoAdSlot(mInstance, this, mFrameLoadListener);
            //sendAnalizeReport(Params.ad_analize, HttpConstant.AD_DATA_SUCCESS);
        } else {
            mAdSlot = new VideoAdSlot(null, this, mFrameLoadListener);
            if (mListener != null) {
                mListener.onAdFailed();
            }
        }
    }

    // release the ad
    public void destory() {
        mAdSlot.destroy();
    }

    @Override
    public ViewGroup getAdParent() {
        return null;
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

    @Override
    public void onClickVideo(String url) {

    }
}

































