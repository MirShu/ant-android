package com.myyg.receiver;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.myyg.R;
import com.myyg.constant.SysEnums;
import com.myyg.model.BizPushMessageModel;
import com.myyg.ui.activity.WinningRecordActivity;
import com.myyg.utils.MyLog;
import com.myyg.utils.NotificationHelper;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by JOHN on 2016/8/6.
 */
public class JPushReceiver extends BroadcastReceiver {
    private final static String TAG = JPushReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        MyLog.e(TAG, "消息推送接收成功");
        String action = intent.getAction();
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            this.handlerBusiness(context, intent);
        }
    }

    /**
     * 处理推送业务
     *
     * @param context
     * @param intent
     */
    private void handlerBusiness(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        MyLog.e(TAG, extra);
        BizPushMessageModel model = JSON.parseObject(extra, BizPushMessageModel.class);
        if (model == null) {
            NotificationHelper.showNotification(context, message);
            return;
        }
//        boolean isForeground = this.isAppOnForeground(context);
//        if (isForeground) {
//            this.handlerForeground(context, bundle);
//            return;
//        }
        Intent intentNotification = new Intent();
        int notify = (int) System.currentTimeMillis();
        if (model.getBizCode() == SysEnums.EnumPushBusiness.Winning.getValue()) {
            intentNotification.setClass(context, WinningRecordActivity.class);
            intentNotification.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // 处理消息通知栏信息
            notify = NotificationHelper.getIdentifier("push_winning");
        }
        PendingIntent toMain = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intentNotification, PendingIntent.FLAG_ONE_SHOT);
        NotificationHelper.showNotification(context, context.getString(R.string.app_name), model.getDisplayMessage(), toMain, notify);
    }

    /**
     * @param context
     * @param bundle
     */
    private void handlerForeground(Context context, Bundle bundle) {
        // 向主页发送广播
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}