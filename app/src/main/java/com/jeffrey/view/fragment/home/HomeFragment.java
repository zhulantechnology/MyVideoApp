package com.jeffrey.view.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeffrey.activity.PhotoViewActivity;
import com.jeffrey.activity.R;
import com.jeffrey.adapter.CourseAdapter;
import com.jeffrey.constant.Constant;
import com.jeffrey.module.recommand.BaseRecommandModel;
import com.jeffrey.module.recommand.RecommandBodyValue;
import com.jeffrey.network.http.RequestCenter;
import com.jeffrey.okhttp.listener.DisposeDataListener;
import com.jeffrey.view.fragment.BaseFragment;
import com.jeffrey.view.home.HomeHeaderLayout;

/**
 * Created by Jun.wang on 2018/5/27.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private Context mContext;

    private static final int REQUEST_QRCODE = 0x01;
    /**
     * UI
     */
    private View mContentView;
    private ListView mListView;
    private TextView mQRCodeView;
    private TextView mCategoryView;
    private TextView mSearchView;
    private ImageView mLoadingView;

    /**
     * data
     */
    private CourseAdapter mAdapter;
    private BaseRecommandModel mRecommandData;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestRecommandData();
    }

    //
    private void requestRecommandData() {
        RequestCenter.requestRecommandData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                // 完成真正的功能逻辑
                mRecommandData = (BaseRecommandModel) responseObj;
                //获得数据之后更新UI
                showSuccessView();
            }

            @Override
            public void onFailure(Object reasonObj) {
                //显示请求失败View
                //showErrorView();
            }
        });
    }

    //显示请求成功UI
    private void showSuccessView() {
        if (mRecommandData.data.list != null && mRecommandData.data.list.size() > 0) {
            mLoadingView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            //为listview添加头
            mListView.addHeaderView(new HomeHeaderLayout(mContext, mRecommandData.data.head));
            // 创建Adapter
            mAdapter = new CourseAdapter(mContext, mRecommandData.data.list);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    mAdapter.updateAdInScrollView();
                }
            });
        } else {
            showErrorView();
        }
    }
    // 请求失败提示
    private void showErrorView() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_home_layout, container, false);
        initView();
        return mContentView;
    }

    private void initView() {
        mQRCodeView = (TextView) mContentView.findViewById(R.id.qrcode_view);
        mQRCodeView.setOnClickListener(this);
        mCategoryView = (TextView) mContentView.findViewById(R.id.category_view);
        mCategoryView.setOnClickListener(this);
        mSearchView = (TextView) mContentView.findViewById(R.id.search_view);
        mSearchView.setOnClickListener(this);
        mListView = (ListView) mContentView.findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        mLoadingView = (ImageView) mContentView.findViewById(R.id.loading_view);
        // 启动LoadingView动画
        AnimationDrawable anim = (AnimationDrawable) mLoadingView.getDrawable();
        anim.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qrcode_view:
                if (hasPermission(Constant.HARDWEAR_CAMERA_PERMISSION)) {
                    doOpenCamera();
                } else {
                    requestPermission(Constant.HARDWEAR_CAMERA_CODE, Constant.HARDWEAR_CAMERA_PERMISSION);
                }
                break;
        }
    }

    @Override
    public void doOpenCamera() {
      //  Intent intent = new Intent(mContext, CaptureActivity.class);
      //  startActivityForResult(intent, REQUEST_QRCODE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecommandBodyValue value = (RecommandBodyValue) mAdapter.getItem(
                            position - mListView.getHeaderViewsCount());
        if (value.type != 0) {
/*            Intent intent = new Intent(mContext, PhotoViewActivity.class);
            intent.putStringArrayListExtra(PhotoViewActivity.PHOTO_LIST, value.url);
            startActivity(intent);*/
        }
    }
}

























