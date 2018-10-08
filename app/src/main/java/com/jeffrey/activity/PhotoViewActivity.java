package com.jeffrey.activity;

import com.jeffrey.activity.base.BaseActivity;
import com.jeffrey.adapter.PhotoPagerAdapter;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jun.wang on 2018/10/8.
 */
public class PhotoViewActivity extends BaseActivity implements View.OnClickListener {
    public static final String PHOTO_LIST = "photo_list";
    // UI
    private ViewPager mPager;
    private TextView mIndictorView;
    private ImageView mShareView;

    // Data
    private PhotoPagerAdapter mAdapter;
    @Override
    public void onClick(View v) {

    }
}























