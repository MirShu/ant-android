package com.myyg.utils;

import android.util.Log;

import com.myyg.R;
import com.myyg.base.BaseApplication;

/**
 * Created by shiyuankao on 2015/12/23.
 */
public class MyLog {

    /**
     * 是否为开发环境
     */
    private static boolean isDeBug = true;

    static {
        isDeBug = Boolean.parseBoolean(BaseApplication.getInstance().getResources().getString(R.string.is_test));
    }

    public static void v(String tag, String msg) {
        writeLog(tag, msg, Log.VERBOSE, null);
    }

    public static void v(String tag, String msg, Throwable tr) {
        writeLog(tag, msg, Log.VERBOSE, tr);
    }

    public static void d(String tag, String msg) {
        writeLog(tag, msg, Log.DEBUG, null);
    }

    public static void d(String tag, String msg, Throwable tr) {
        writeLog(tag, msg, Log.DEBUG, tr);
    }

    public static void i(String tag, String msg) {
        writeLog(tag, msg, Log.INFO, null);
    }

    public static void i(String tag, String msg, Throwable tr) {
        writeLog(tag, msg, Log.INFO, tr);
    }

    public static void w(String tag, String msg) {
        writeLog(tag, msg, Log.WARN, null);
    }

    public static void w(String tag, String msg, Throwable tr) {
        writeLog(tag, msg, Log.WARN, tr);
    }

    public static void w(String tag, Throwable tr) {
        writeLog(tag, "", Log.WARN, tr);
    }

    public static void e(String tag, String msg) {
        writeLog(tag, msg, Log.ERROR, null);
    }

    public static void e(String tag, String msg, Throwable tr) {
        writeLog(tag, msg, Log.ERROR, tr);
    }

    /**
     * @param tag
     * @param msg
     * @param level
     * @param tr
     */
    private static void writeLog(String tag, String msg, int level, Throwable tr) {
        if (!isDeBug) {
            return;
        }
        switch (level) {
            case Log.VERBOSE:
                Log.v(tag, msg, tr);
                break;
            case Log.DEBUG:
                Log.d(tag, msg, tr);
                break;
            case Log.INFO:
                Log.i(tag, msg, tr);
                break;
            case Log.WARN:
                Log.w(tag, msg, tr);
                break;
            case Log.ERROR:
                Log.e(tag, msg, tr);
                break;
        }
    }
}
