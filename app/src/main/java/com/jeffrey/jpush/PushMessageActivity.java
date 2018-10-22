package com.jeffrey.jpush;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.jeffrey.activity.AdBrowerActivity;
import com.jeffrey.activity.R;
import com.jeffrey.activity.base.BaseActivity;
import com.jeffrey.module.PushMessage;


/**
 * Created by Jun.wang on 2018/10/22.
 */
public class PushMessageActivity extends BaseActivity {

    // UI
    private TextView mTypeView;
    private TextView mTypeValueView;
    private TextView mContentView;
    private TextView mContentValueView;

    // data
    private PushMessage mPushMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jpush_layout);
    }

    // 初始化推送过来的数据
    private void initData() {
        Intent intent = getIntent();
        mPushMessage = (PushMessage) intent.getSerializableExtra("pushMessage");
    }

    private void initView() {
        mTypeView = (TextView) findViewById(R.id.message_type_view);
        mTypeValueView = (TextView) findViewById(R.id.message_type_value_view);
        mContentView = (TextView) findViewById(R.id.message_content_view);
        mContentValueView = (TextView) findViewById(R.id.message_content_value_view);

        mTypeValueView.setText(mPushMessage.messageType);
        mContentValueView.setText(mPushMessage.messageContent);
        if (!TextUtils.isEmpty(mPushMessage.messageUrl)) {
            //跳转到web页面
            gotoWebView();
        }
    }

    private void gotoWebView() {
        Intent intent = new Intent(this, AdBrowerActivity.class);
        intent.putExtra(AdBrowerActivity.KEY_URL, mPushMessage.messageUrl);
        startActivity(intent);
        finish();
    }
}
