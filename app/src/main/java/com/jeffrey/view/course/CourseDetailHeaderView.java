package com.jeffrey.view.course;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.jeffrey.adutil.ImageLoaderUtil;
import com.jeffrey.module.course.CourseHeaderValue;

/**
 * Created by Jun.wang on 2018/10/17.
 */

public class CourseDetailHeaderView extends RelativeLayout {

    private Context mContext;

    private CourseHeaderValue mData;
    private ImageLoaderUtil mImageLoader;

    public CourseDetailHeaderView(Context context, CourseHeaderValue value) {
        this(context, null, value);
    }


    public CourseDetailHeaderView(Context context, AttributeSet attrs, CourseHeaderValue value) {
        super(context, attrs);
        mContext = context;
        mData = value;
        mImageLoader = ImageLoaderUtil.getInstance(context);
        initView();
    }
}


















