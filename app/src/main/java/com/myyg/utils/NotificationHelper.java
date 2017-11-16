package com.myyg.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.widget.RemoteViews;

import com.myyg.R;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JOHN on 2016/8/6.
 */
public class NotificationHelper {
    private static final String TAG = NotificationHelper.class.getSimpleName();

    /**
     *
     */
    public static ConcurrentHashMap<String, Integer> notificationMap = new ConcurrentHashMap<>();

    static {

    }

    /**
     * @param context
     * @param msgContent
     * @param i
     */
    public static void sendNotification(Context context, String msgContent, int i) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification myNotify = new Notification();
        myNotify.icon = R.mipmap.ic_launcher;
        myNotify.tickerText = context.getString(R.string.notice_tip);
        myNotify.when = System.currentTimeMillis();
        myNotify.flags = Notification.FLAG_AUTO_CANCEL;// 不能够自动清除
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.view_notification);
        rv.setTextViewText(R.id.tv_content, msgContent);
        rv.setTextViewText(R.id.tv_title, context.getString(R.string.app_name));
        myNotify.defaults = Notification.DEFAULT_SOUND;
        myNotify.contentView = rv;
        nm.notify(i, myNotify);
    }

    /**
     * @param context
     * @param msgContent
     * @param pi
     * @param i
     */
    public static void sendNotification(Context context, String msgContent, PendingIntent pi, int i) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification myNotify = new Notification();
        myNotify.icon = R.mipmap.ic_launcher;
        myNotify.tickerText = context.getString(R.string.notice_tip);
        myNotify.when = System.currentTimeMillis();
        myNotify.contentIntent = pi;
        myNotify.flags = Notification.FLAG_AUTO_CANCEL;// 不能够自动清除
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.view_notification);
        rv.setTextViewText(R.id.tv_content, msgContent);
        rv.setTextViewText(R.id.tv_title, context.getString(R.string.app_name));
        myNotify.defaults = Notification.DEFAULT_SOUND;
        myNotify.contentView = rv;
        nm.notify(i, myNotify);
    }

    /**
     * @param context
     * @param msgContent
     * @param i
     */
    public static void userNotification(Context context, String msgContent, int i) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification myNotify = new Notification();
        myNotify.icon = R.mipmap.ic_launcher;
        myNotify.tickerText = context.getString(R.string.notice_tip);
        myNotify.when = System.currentTimeMillis();
        myNotify.flags = Notification.FLAG_AUTO_CANCEL;// 不能够自动清除
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.view_notification);
        rv.setTextViewText(R.id.tv_content, msgContent);
        rv.setTextViewText(R.id.tv_title, context.getString(R.string.app_name));
        myNotify.defaults = Notification.DEFAULT_SOUND;
        myNotify.contentView = rv;
        nm.notify(i, myNotify);
    }

    /**
     * @param context
     * @param content
     */
    public static void showNotification(Context context, String content) {
        String title = context.getString(R.string.app_name);
        showNotification(context, title, content, null, (int) System.currentTimeMillis());
    }

    /**
     * @param context
     * @param title
     * @param content
     */
    public static void showNotification(Context context, String title, String content) {
        showNotification(context, title, content, null, (int) System.currentTimeMillis());
    }

    /**
     * @param context
     * @param title
     * @param content
     * @param pi
     */
    public static void showNotification(Context context, String title, String content, PendingIntent pi) {
        showNotification(context, title, content, pi, (int) System.currentTimeMillis());
    }

    /**
     * 发送系统消息通知
     *
     * @param context 上下文
     * @param title   标题
     * @param content 内容
     * @param pi      点击跳转
     * @param i       消息数目
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotification(Context context, String title, String content, PendingIntent pi, int i) {
        Notification notification = new Notification.Builder(context)
                .setLargeIcon(getAppIcon(context))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(context.getString(R.string.notice_tip))
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL).build();
        if (pi != null) {
            notification.contentIntent = pi;
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(i, notification);
    }

    /**
     * 获取APP图标
     *
     * @return
     */
    private static Bitmap getAppIcon(Context context) {
        BitmapDrawable bitmapDrawable;
        Bitmap appIcon;
        bitmapDrawable = (BitmapDrawable) context.getApplicationInfo().loadIcon(context.getPackageManager());
        appIcon = bitmapDrawable.getBitmap();
        return appIcon;
    }

    /**
     * 获取通知唯一标识
     *
     * @param businessCode
     * @return
     */
    public static int getIdentifier(String businessCode) {
        if (notificationMap.containsKey(businessCode)) {
            return notificationMap.get(businessCode);
        }
        int notify = notificationMap.size() + 1001;
        return notify;
    }
}
