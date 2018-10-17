package com.jeffrey.module.course;

import com.jeffrey.module.BaseModel;

/**
 * Created by Jun.wang on 2018/10/17.
 */
public class CourseCommentValue extends BaseModel {
    public String text;
    public String name;
    public String logo;
    public int type;
    public String userId; //评论所属用户ID
}
