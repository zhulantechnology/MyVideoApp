package com.jeffrey.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jeffrey.application.MyApplication;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by Jun.wang on 2018/9/30.
 */

// 配置文件工具类
public class SPManager {
    private static SharedPreferences sp = null;
    private static SPManager spManager = null;
    private static Editor editor = null;

    // preference文件名，因为是文件，最好加上后缀，.pre
    public static final String SHARED_PREFERENCE_NAME = "video.pre";
    public static final String VIDEO_PLAY_SETTING = "video_play_setting";
    public static final String IS_SHOW_GUIDE = "is_show_guide";

    private SPManager() {
        sp = MyApplication.getInstance().getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static SPManager getInstance() {
        if (spManager == null || sp == null || editor == null) {
            spManager = new SPManager();
        }
        return spManager;
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void putLong(String key, Long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key, int defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    private boolean isKeyExist(String key) {
        return sp.contains(key);
    }

    private void remove(String key) {
        editor.remove(key);
        editor.commit();
    }
}





























