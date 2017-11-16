package com.myyg.javascript;

import android.app.Activity;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.myyg.utils.UIHelper;

/**
 * Created by shiyuankao on 2015/12/25.
 */
public class BaseJavaScript {
    protected Activity mContext;
    protected Handler mHandler;

    /**
     * @param mContext
     */
    public BaseJavaScript(Activity mContext) {
        this.mContext = mContext;
        this.mHandler = new Handler();
    }

    /**
     * 显示提示信息
     *
     * @param message
     */
    @JavascriptInterface
    public void toastMessage(final String message) {
        this.mHandler.post(() -> UIHelper.toastMessage(mContext, message));
    }
}
