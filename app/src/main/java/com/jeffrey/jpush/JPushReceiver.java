package com.jeffrey.jpush;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.jeffrey.activity.HomeActivity;
import com.jeffrey.activity.LoginActivity;
import com.jeffrey.adutil.ResponseEntityToModule;
import com.jeffrey.manager.UserManager;
import com.jeffrey.module.PushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Jun.wang on 2018/10/22.
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = JPushReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())){
            // 接收到推送下来的通知的ID
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
            PushMessage pushMessage = (PushMessage)ResponseEntityToModule.parseJsonToModule(
                        bundle.getString(JPushInterface.EXTRA_EXTRA), PushMessage.class);
          //  PushMessage pushMessage = (PushMessage) ResponseEntityToModule
           //         .parseJsonToModule(bundle.getString(JPushInterface.EXTRA_EXTRA), PushMessage.class);

            // 如果应用已经启动，无论前台还是后台
            if (getCurrentTask(context)) {
                Intent pushIntent = new Intent();
                pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pushIntent.putExtra("pushMessage", pushMessage);
                // 需要登陆且当前没有登陆，去登陆页面
                if (pushMessage.messageType != null && pushMessage.messageType.equals("2")
                        && ! UserManager.getInstance().hasLogined()) {
                        pushIntent.setClass(context, LoginActivity.class);
                        pushIntent.putExtra("fromPush", true);
                } else {
                    // 不需要登陆或者已经登陆， 直接跳转到内容显示页面
                    pushIntent.setClass(context, PushMessageActivity.class);
                }
                context.startActivity(pushIntent);
            } else {
                // 应用没有启动， 启动应用主页, HomeActivity
                Intent mainIntent = new Intent(context, HomeActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //需要登陆
                if (pushMessage.messageType != null && pushMessage.messageType.equals("2")) {
                    Intent loginIntent = new Intent();
                    loginIntent.setClass(context, LoginActivity.class);
                    loginIntent.putExtra("fromPush", true);
                    loginIntent.putExtra("pushMessage", pushMessage);
                    context.startActivities(new Intent[]{mainIntent, loginIntent});
                } else {
                    Intent pushIntent = new Intent(context, PushMessageActivity.class);
                    pushIntent.putExtra("pushMessage", pushMessage);
                    context.startActivities(new Intent[]{mainIntent, pushIntent});
                }

            }
        }
    }

    // 打印所有的intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    // no extra data
                    continue;
                }
                try {
                    // 先将JSON字符串转化为对象， 再取其中的字段
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" + myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                }
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }

        return sb.toString();
    }


    // 获取指定包名的应用程序是否在运行
    private boolean getCurrentTask(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> appProcessInfos = activityManager.getRunningTasks(50);
        for (RunningTaskInfo process : appProcessInfos) {
            if (process.baseActivity.getPackageName().equals(context.getPackageName())
                    || process.topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
