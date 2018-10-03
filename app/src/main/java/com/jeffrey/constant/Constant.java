package com.jeffrey.constant;

import android.os.Environment;

import java.security.PublicKey;
import java.util.jar.Manifest;

/**
 * APP 所使用到的常量
 * Created by Jun.wang on 2018/10/3.
 */

public class Constant {
    // 权限常量相关
    public static final int WRITE_READ_EXTERNAL_CODE = 0x01;
    public static final String[] WRITE_READ_EXTERNAL_PERMISSION = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final int HARDWEAR_CAMERA_CODE = 0x02;
    public static final String[] HARDWEAR_CAMERA_PERMISSION = new String[]
                                {android.Manifest.permission.CAMERA};

    //整个应用文件下载保存路径
    public static String APP_PHOTO_DIR = Environment.
            getExternalStorageDirectory().getAbsolutePath().concat("/imooc_business/photo/");
}

























