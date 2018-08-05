package com.jeffrey.module.recommand;

import com.jeffrey.module.BaseModel;

import java.util.ArrayList;

/**
 * Created by Jun.wang on 2018/6/4.
 */
public class RecommandModel extends BaseModel {

    // 分别对应我们json中两个数据部分
    public RecommandHeadValue head;
    public ArrayList<RecommandBodyValue> list;
}
