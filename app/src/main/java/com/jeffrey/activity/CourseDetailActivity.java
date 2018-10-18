package com.jeffrey.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeffrey.activity.base.BaseActivity;
import com.jeffrey.adapter.CourseCommentAdapter;
import com.jeffrey.module.course.BaseCourseModel;
import com.jeffrey.network.http.RequestCenter;
import com.jeffrey.okhttp.listener.DisposeDataHandle;
import com.jeffrey.okhttp.listener.DisposeDataListener;
import com.jeffrey.util.Util;
import com.jeffrey.view.course.CourseDetailFooterView;
import com.jeffrey.view.course.CourseDetailHeaderView;

/**
 * 课程详情界面，需要用SingleTop模式
 * Created by Jun.wang on 2018/10/17.
 */
public class CourseDetailActivity extends BaseActivity implements View.OnClickListener{

    public static String COURSE_ID = "courseID";
    // Data
    private String mCourseID;
    private BaseCourseModel mData;
    private String tempHint = "";

    private CourseCommentAdapter mAdapter;

    //UI
    private ImageView mBackView;
    private ListView mListView;
    private ImageView mLoadingView;
    private RelativeLayout mBottomLayout;
    private ImageView mJianPanView;
    private EditText mInputEditView;
    private TextView mSendView;

    private CourseDetailHeaderView headerView;
    private CourseDetailFooterView footerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail_layout);
        initData();
        initView();
        requestDetail();
    }

    // 初始化数据
    private void initData() {
        Intent intent = getIntent();
        mCourseID = intent.getStringExtra(COURSE_ID);
    }

    private void initView() {
        mBackView = (ImageView) findViewById(R.id.back_view);
        mBackView.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.comment_list_view);
        //mListView.setOnItemClickListener(this);
        mListView.setVisibility(View.GONE);
        mLoadingView = (ImageView) findViewById(R.id.loading_view);
        mLoadingView.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingView.getDrawable();
        anim.start();

        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mJianPanView = (ImageView) findViewById(R.id.jianpan_view);
        mJianPanView.setOnClickListener(this);
        mInputEditView = (EditText) findViewById(R.id.comment_edit_view);
        mSendView = (TextView) findViewById(R.id.send_view);
        mSendView.setOnClickListener(this);
        mBottomLayout.setVisibility(View.GONE);
        intoEmptyState();
    }

    private void intoEmptyState() {
        tempHint = "";
        mInputEditView.setText("");
        mInputEditView.setHint("请输入留言");
        Util.hideSoftInputMethod(this, mInputEditView);
    }

    private void requestDetail() {
        RequestCenter.requestCourseDetail(mCourseID, new DisposeDataListener(){

            @Override
            public void onSuccess(Object responseObj) {
                mData = (BaseCourseModel) responseObj;
                updateUI();
            }

            @Override
            public void onFailure(Object reasonObj) {

            }
        });
    }

    // 根据数据填充UI
    private void updateUI() {
        mLoadingView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        mAdapter = new CourseCommentAdapter(this, mData.data.body);
        mListView.setAdapter(mAdapter);

        if (headerView != null) {
            mListView.removeHeaderView(headerView);
        }
        headerView = new CourseDetailHeaderView(this, mData.data.head);
        mListView.addHeaderView(headerView);

        if (footerView != null) {
            mListView.removeFooterView(footerView);
        }
        footerView = new CourseDetailFooterView(this, mData.data.footer);
        mListView.addFooterView(footerView);

        mBottomLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

    }
}























