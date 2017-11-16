package com.myyg.listener;

/**
 * Created by JOHN on 2016/6/7.
 */
public interface OnLoginVerifyListener {
    /**
     * 验证用户登录回调
     *
     * @param isLogin  是否登录
     * @param tabIndex 当前选项卡索引
     */
    void onCallback(boolean isLogin, int tabIndex);
}
