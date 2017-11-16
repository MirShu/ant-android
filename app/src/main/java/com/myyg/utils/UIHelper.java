package com.myyg.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myyg.R;
import com.myyg.listener.OnDialogListener;
import com.myyg.widget.LoadingDialog;
import com.rey.material.app.Dialog;

/**
 * Created by JOHN on 2015/11/10.
 */
public class UIHelper {
    /**
     *
     */
    private static LoadingDialog loading;

    private static Handler mHandler = new Handler();

    /**
     * 弹出Toast消息
     */
    public static void toastMessage(Context context, String msg) {
        toastMessage(context, msg, 1500);
    }

    /**
     * @param context
     * @param resId
     */
    public static void toastMessage(Context context, int resId) {
        toastMessage(context, resId, 1500);
    }

    /**
     * 正常启动Activity
     */
    public static void startActivity(Activity activity, Class<? extends Activity> clazz) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
    }

    /**
     * @param context
     * @param intent
     */
    public static void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }

    /**
     * 携带数据启动Activity
     */
    public static void startActivity(Activity activity, Class<? extends Activity> clazz, Bundle bundle) {
        Intent intent = new Intent(activity, clazz);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 启动Activity，获取数据
     */
    public static void startActivityForResult(Activity activity, Class<? extends Activity> clazz, int requestCode, Bundle bundle) {
        try {
            Intent intent = new Intent(activity, clazz);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception ex) {

        }
    }

    /**
     * @param activity
     * @param intent
     * @param requestCode
     * @param bundle
     */
    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, Bundle bundle) {
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 弹出拨打电话Activity
     *
     * @param context
     * @param phone
     */
    public static void showTel(Activity context, String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    /**
     * 显示加载对话框
     */
    public static void showLoading(Context context) {
        if (loading == null) {
            loading = new LoadingDialog(context);
        }
        loading.show();
    }

    /**
     * 显示加载对话框
     *
     * @param message
     */
    public static void showLoading(Context context, String message) {
        if (loading == null) {
            loading = new LoadingDialog(context);
        }
        loading.show();
    }

    /**
     * 隐藏加载对话框
     */
    public static void hideLoading() {
        if (loading != null) {
            loading.dismiss();
            loading = null;
        }
    }

    /**
     * 隐藏加载对话框
     *
     * @param second 多少秒钟后隐藏
     */
    public static void hideLoading(int second) {
        mHandler.postDelayed(() -> hideLoading(), second * 1000);
    }

    /**
     * @param mContext
     * @param resId
     * @param duration
     */
    public static void toastMessage(Context mContext, int resId, int duration) {
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(duration);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.view_toast, null);
        TextView tv_toast = (TextView) view.findViewById(R.id.tv_toast);
        tv_toast.setText(resId);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 200);
        tv_toast.setLayoutParams(layoutParams);
        toast.setView(view);
        toast.show();
    }

    /**
     * @param mContext
     * @param msg
     * @param duration
     */
    private static void toastMessage(Context mContext, CharSequence msg, int duration) {
        msg = msg.toString().replace("。", "").replace("，", ",");
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(duration);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.view_toast, null);
        TextView tv_toast = (TextView) view.findViewById(R.id.tv_toast);
        tv_toast.setText(msg);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 200);
        tv_toast.setLayoutParams(layoutParams);
        toast.setView(view);
        toast.show();
    }

    /**
     * @param context
     * @param title
     * @param buttonTitle
     */
    public static Dialog showDialog(Context context, String title, String buttonTitle) {
        return showDialog(context, title, "", buttonTitle, null);
    }

    /**
     * @param context
     * @param title
     * @param leftTitle
     * @param rightTitle
     * @param listener
     */
    public static Dialog showDialog(Context context, String title, String leftTitle, String rightTitle, final OnDialogListener listener) {
        return showDialog(context, title, leftTitle, rightTitle, 0, listener);
    }

    /**
     * @param context
     * @param title
     * @param leftTitle
     * @param rightTitle
     * @param contentView
     * @param listener
     */
    public static Dialog showDialog(Context context, String title, String leftTitle, String rightTitle, int contentView, final OnDialogListener listener) {
        try {
            title = title.toString().replace("。", "").replace("，", ",");
            final Dialog mDialog = new Dialog(context, R.style.SimpleDialogLight);
            mDialog.title(title);
            mDialog.positiveAction(rightTitle);
            mDialog.negativeAction(leftTitle);
            //mDialog.titleSize(16);
            mDialog.setCancelable(false);
            if (contentView != 0) {
                mDialog.setContentView(contentView);
            }
            mDialog.positiveActionClickListener(v -> {
                mDialog.dismiss();
                if (listener != null) {
                    listener.ok(mDialog);
                }
            });
            mDialog.negativeActionClickListener(v -> {
                mDialog.dismiss();
                if (listener != null) {
                    listener.cancel(mDialog);
                }
            });
            mDialog.show();
            return mDialog;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * @param context
     * @param title
     * @param leftTitle
     * @param rightTitle
     * @param contentView
     * @param listener
     */
    public static Dialog showDialog(Context context, String title, String leftTitle, String rightTitle, View contentView, final OnDialogListener listener) {
        title = title.toString().replace("。", "").replace("，", ",");
        final Dialog mDialog = new Dialog(context, R.style.SimpleDialogLight);
        mDialog.title(title);
        mDialog.positiveAction(rightTitle);
        mDialog.negativeAction(leftTitle);
        mDialog.setContentView(contentView);
        //mDialog.titleSize(16);
        mDialog.setCancelable(false);
        mDialog.positiveActionClickListener(v -> {
            mDialog.dismiss();
            if (listener != null) {
                listener.ok(mDialog);
            }
        });
        mDialog.negativeActionClickListener(v -> {
            mDialog.dismiss();
            if (listener != null) {
                listener.cancel(mDialog);
            }
        });
        mDialog.show();
        return mDialog;
    }

    /**
     * @param context
     * @param title
     * @param rightTitle
     * @param contentView
     * @param listener
     */
    public static Dialog showDialog(Context context, String title, String rightTitle, View contentView, final OnDialogListener listener) {
        title = title.toString().replace("。", "").replace("，", ",");
        final Dialog mDialog = new Dialog(context, R.style.SimpleDialogLight);
        mDialog.title(title);
        mDialog.positiveAction(rightTitle);
        mDialog.setCanceledOnTouchOutside(false);//点击屏幕不消失
        mDialog.setContentView(contentView);
        //mDialog.titleSize(16);
        mDialog.positiveActionClickListener(v -> {
            if (listener != null) {
                listener.ok(mDialog);
            }
        });
        mDialog.show();
        return mDialog;
    }

    /**
     * @param context
     * @param contentView
     */
    public static Dialog showDialog(Context context, View contentView) {
        final Dialog mDialog = new Dialog(context, R.style.SimpleDialogLight);
        mDialog.setContentView(contentView);
        //mDialog.titleSize(16);
        mDialog.show();
        return mDialog;
    }
}
