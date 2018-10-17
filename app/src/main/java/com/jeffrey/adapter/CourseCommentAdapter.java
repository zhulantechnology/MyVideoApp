package com.jeffrey.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jeffrey.activity.R;
import com.jeffrey.module.course.CourseCommentValue;
import com.jeffrey.util.ImageLoaderManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 课程详情的ListView适配器
 * Created by Jun.wang on 2018/10/17.
 */
public class CourseCommentAdapter extends BaseAdapter {

    private LayoutInflater mInflate;
    private Context mContext;

    private ArrayList<CourseCommentValue> mData;
    private ViewHolder mViewHolder;
    private ImageLoaderManager mImagerLoader;

    public CourseCommentAdapter(Context context, ArrayList<CourseCommentValue> data) {
        mContext = context;
        mData = data;
        mInflate = LayoutInflater.from(mContext);
        mImagerLoader = ImageLoaderManager.getInstance(mContext);
    }

    private static class ViewHolder {
        CircleImageView mImageView;
        TextView mNameView;
        TextView mCommentView;
        TextView mOwnerView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CourseCommentValue value = (CourseCommentValue) getItem(position);
        // 无Tag时
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflate.inflate(R.layout.item_comment_layout, parent, false);
            mViewHolder.mImageView = convertView.findViewById(R.id.photo_view);
            mViewHolder.mNameView = convertView.findViewById(R.id.name_view);
            mViewHolder.mCommentView = convertView.findViewById(R.id.text_view);
            mViewHolder.mOwnerView = convertView.findViewById(R.id.owner_view);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        //填充数据
        if (value.type == 0) {
            mViewHolder.mOwnerView.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.mOwnerView.setVisibility(View.GONE);
        }

        mImagerLoader.displayImage(mViewHolder.mImageView, value.logo);
        mViewHolder.mNameView.setText(value.name);
        mViewHolder.mCommentView.setText(value.text);
        return convertView;
    }

    public void addComment(CourseCommentValue value) {
        mData.add(0,value);
        notifyDataSetChanged();
    }

    public int getCommentCount() {
        return mData.size();
    }
}
