package com.myyg.utils;

/**
 * Created by shiyuankao on 2016/4/2.
 */
public class ClickFilter {
    public static final long INTERVAL = 500L; //防止连续点击的时间间隔

    private static long lastClickTime = 0L; //上一次点击的时间

    /**
     * @return
     */
    public static boolean filter() {
        long time = System.currentTimeMillis();
        if ((time - lastClickTime) < INTERVAL) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}