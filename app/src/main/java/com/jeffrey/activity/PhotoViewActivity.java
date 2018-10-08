package com.jeffrey.activity;

import com.jeffrey.activity.base.BaseActivity;
import com.jeffrey.adapter.PhotoPagerAdapter;
import com.jeffrey.adutil.Utils;
import com.jeffrey.util.Util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
    private ArrayList<String> mPhotoLists;
    private int mLength;
    private int currentPos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view_layout);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mPhotoLists = intent.getStringArrayListExtra(PHOTO_LIST);
        mLength = mPhotoLists.size();
    }

    private void initView() {
        mIndictorView = (TextView) findViewById(R.id.indictor_view);
        mIndictorView.setText("1/" + mLength);
        mShareView = (ImageView) findViewById(R.id.share_view);
        mShareView.setOnClickListener(this);
        mPager = (ViewPager) findViewById(R.id.photo_pager);
        mAdapter = new PhotoPagerAdapter(this, mPhotoLists, false);
        mPager.setPageMargin(Utils.dip2px(this, 30));
        mPager.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {

    }
}























