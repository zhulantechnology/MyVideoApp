package com.jeffrey.view.home;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeffrey.activity.R;
import com.jeffrey.adutil.ImageLoaderUtil;
import com.jeffrey.module.recommand.RecommandFooterValue;


/**
 * @author: Jun.wang 2018-08-08
 * @function:   ListView的底部
 */
public class HomeBottomItem extends RelativeLayout {

    private Context mContext;
    /**
     * UI
     */
    private RelativeLayout mRootView;
    private TextView mTitleView;
    private TextView mInfoView;
    private TextView mInterestingView;
    private ImageView mImageOneView;
    private ImageView mImageTwoView;

    /**
     * Data
     */
    private RecommandFooterValue mData;
    private ImageLoaderUtil mImageLoader;

    public HomeBottomItem(Context context, RecommandFooterValue data) {
        this(context, null, data);
    }

    // 在构造方法中接受传入的数据和初始化View
    public HomeBottomItem(Context context, AttributeSet attrs, RecommandFooterValue data) {
        super(context, attrs);
        mContext = context;
        mData = data;
        mImageLoader = ImageLoaderUtil.getInstance(mContext);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mRootView = (RelativeLayout) inflater.inflate(R.layout.item_home_recommand_layout, this); //添加到当前布局中
        mTitleView = (TextView) mRootView.findViewById(R.id.title_view);
        mInfoView = (TextView) mRootView.findViewById(R.id.info_view);
        mInterestingView = (TextView) mRootView.findViewById(R.id.interesting_view);
        mImageOneView = (ImageView) mRootView.findViewById(R.id.icon_1);
        mImageTwoView = (ImageView) mRootView.findViewById(R.id.icon_2);
        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                /*Intent intent = new Intent(mContext, AdBrowserActivity.class);
//                intent.putExtra(AdBrowserActivity.KEY_URL, mData.destationUrl);
//                mContext.startActivity(intent);*/  //wangjun delete temporary
            }
        });
        mTitleView.setText(mData.title);
        mInfoView.setText(mData.info);
        mInterestingView.setText(mData.from);
        mImageLoader.displayImage(mImageOneView, mData.imageOne);
        mImageLoader.displayImage(mImageTwoView, mData.imageTwo);
    }
}
