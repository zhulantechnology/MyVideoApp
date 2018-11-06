package com.jeffrey.view.fragment;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.jeffrey.constant.Constant;

/**
 * Created by Jun.wang on 2018/5/27.
 * 为所有的Fragment提供一些公共的行为或事件
 */

public class BaseFragment extends Fragment {
    // 申请指定的权限
    public void requestPermission(int code, String... permissions) {
        if(Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissions, code);
        }
    }

    //判断是否有指定的权限
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant.HARDWEAR_CAMERA_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doOpenCamera();
                }
                break;
            case Constant.WRITE_READ_EXTERNAL_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doWriteSDCard();
                }
                break;
        }
    }

    // 空实现，有子类去实现
    public void doOpenCamera() {

    }
    // 空实现，有子类去实现
    public void doWriteSDCard() {

    }
}





























