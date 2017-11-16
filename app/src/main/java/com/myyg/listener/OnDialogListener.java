package com.myyg.listener;

import com.rey.material.app.Dialog;

/**
 * Created by shiyuankao on 2016/3/25.
 */
public interface OnDialogListener {
    /**
     * 确定
     *
     * @param dialog
     */
    void ok(Dialog dialog);

    /**
     * 取消
     *
     * @param dialog
     */
    void cancel(Dialog dialog);
}
