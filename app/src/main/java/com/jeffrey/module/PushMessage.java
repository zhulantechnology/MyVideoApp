package com.jeffrey.module;

/**
 * 极光推送消息实体，包含所有的数据字段
 * Created by Jun.wang on 2018/10/22.
 */
public class PushMessage extends BaseModel {
    // 消息类型
    public String messageType = null;
    // 链接
    public String messageUrl = null;
    // 详情内容
    public String messageContent = null;
}
