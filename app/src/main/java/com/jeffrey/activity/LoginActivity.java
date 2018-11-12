package com.jeffrey.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jeffrey.activity.base.BaseActivity;
import com.jeffrey.manager.UserManager;
import com.jeffrey.module.user.User;
import com.jeffrey.network.http.RequestCenter;
import com.jeffrey.okhttp.listener.DisposeDataListener;

/**
 * Created by Jun.wang on 2018/10/7.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    // 自定义登录广播Action
    public static final String LOGIN_ACTION = "com.imooc.action.LOGIN_ACTION";

    private EditText mUserNameAssociateView;
    private EditText mPasswordView;
    private TextView mLoginView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        initView();

    }

    private void initView() {
        // changeStatusBarColor(R.color.white);
        mUserNameAssociateView = (EditText) findViewById(R.id.associate_email_input);
        mPasswordView = (EditText) findViewById(R.id.login_input_password);
        mLoginView = (TextView) findViewById(R.id.login_button);
        mLoginView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                login();
                break;
        }
    }

    private void login() {
        String userName = mUserNameAssociateView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        RequestCenter.login(userName, password, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                User user = (User) responseObj;
                UserManager.getInstance().setUser(user);
                sendLoginBroadcast();
                finish();
            }

            @Override
            public void onFailure(Object reasonObj) {

            }
        });
    }

    // 向整个应用发送登录广播事件
    private void sendLoginBroadcast() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(LOGIN_ACTION));
    }

}






















