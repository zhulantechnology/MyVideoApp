package com.jeffrey.adutil;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.jeffrey.constant.SDKConstant.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.Manifest.permission;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;


/**
 * @author qndroid
 */
public class Utils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale);
    }

    // decide whether the video could autoplay
    public static boolean canAutoPlay(Context context, AutoPlaySetting setting) {
        boolean result = true;
        switch (setting) {
            case AUTO_PLAY_3G_4G_WIFI:
                result = true;
                break;
            case AUTO_PLAY_ONLY_WIFI:
                if (isWifiConnected(context)) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            case AUTO_PLAY_NEVER:
                result = false;
                break;
        }
        return result;
    }

    // is wifi connected
    public static boolean isWifiConnected(Context context) {
        if (context.checkCallingOrSelfPermission(permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
                return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }

        return false;
    }

    public static int getVisiblePercent(View pView) {
        if (pView != null && pView.isShown()) {
            DisplayMetrics displayMetrics = pView.getContext().getResources().getDisplayMetrics();
            int displayWidth = displayMetrics.widthPixels;
            Rect rect = new Rect();
            pView.getGlobalVisibleRect(rect);
            if ((rect.top > 0) && (rect.left < displayWidth)) {
                double areaVisible = rect.width() * rect.height();
                double areaTotal = pView.getWidth() * pView.getHeight();
                return (int)((areaVisible / areaTotal) * 100);
            } else {
                return -1;
            }
        }

        return -1;
    }

    // 获取View的屏幕属性
    public static final String PROPNAME_SCREENLOCATION_LEFT = "propname_screenlocation_left";
    public static final String PROPNAME_SCREENLOCATION_TOP = "proname_screenlocation_top";
    public static final String PROPNAME_WIDTH = "propname_width";
    public static final String PROPNAME_HEIGHT = "propname_height";

    public static Bundle getViewProperty(View view) {
        Bundle bundle = new Bundle();
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        bundle.putInt(PROPNAME_SCREENLOCATION_LEFT, screenLocation[0]);
        bundle.putInt(PROPNAME_SCREENLOCATION_TOP,screenLocation[1]);
        bundle.putInt(PROPNAME_HEIGHT, view.getWidth());
        bundle.putInt(PROPNAME_HEIGHT, view.getHeight());
                + " Width: " + view.getWidth() + " Height: " + view.getHeight());

        return bundle;
    }

}
















