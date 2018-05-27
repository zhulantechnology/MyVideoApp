package com.jeffrey.view.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeffrey.activity.R;
import com.jeffrey.view.fragment.BaseFragment;

/**
 * Created by Jun.wang on 2018/5/27.
 */

public class MessageFragment extends BaseFragment {
    private View mContentView;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_message_layout, container, false);
        return mContentView;
    }
}
