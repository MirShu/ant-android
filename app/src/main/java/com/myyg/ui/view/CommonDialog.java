package com.myyg.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.myyg.R;

/**
 * Created by JOHN on 2015/11/30.
 */
public class CommonDialog {
    private Dialog dialog;
    private Button btnSubmit;
    private TextView tv_title;
    private TextView tv_content;
    private String buttonText = "确定";
    private String title;
    private String content;
    private boolean isCancelable = true;

    /**
     * @param mContext
     */
    public CommonDialog(Context mContext) {
        this.buttonText = "确定";
        this.createView(mContext);
    }

    /**
     * @param mContext
     * @param buttonText
     */
    public CommonDialog(Context mContext, String buttonText) {
        this.buttonText = buttonText;
        this.createView(mContext);
    }


    /**
     * @param mContext
     * @param content
     */
    public CommonDialog(Context mContext, String content, OnCommonButtonListener buttonListener) {
        this.buttonText = "确定";
        this.content = content;
        this.createView(mContext);
        this.setButtonListener(buttonListener);
    }

    /**
     * @param mContext
     * @param content
     * @param isCancelable
     * @param buttonListener
     */
    public CommonDialog(Context mContext, String content, boolean isCancelable, OnCommonButtonListener buttonListener) {
        this.buttonText = "确定";
        this.content = content;
        this.isCancelable = isCancelable;
        this.createView(mContext);
        this.setButtonListener(buttonListener);
    }

    /**
     * @param mContext
     * @param title
     * @param content
     */
    public CommonDialog(Context mContext, String title, String content) {
        this.buttonText = "确定";
        this.title = title;
        this.content = content;
        this.createView(mContext);
        this.createView(mContext);
    }

    /**
     * @param mContext
     * @param title
     * @param content
     */
    public CommonDialog(Context mContext, String title, String content, OnCommonButtonListener buttonListener) {
        this.buttonText = "确定";
        this.title = title;
        this.content = content;
        this.createView(mContext);
        this.setButtonListener(buttonListener);
    }

    /**
     * @param mContext
     * @param buttonText
     * @param title
     * @param content
     */
    public CommonDialog(Context mContext, String buttonText, String title, String content) {
        this.buttonText = buttonText;
        this.title = title;
        this.content = content;
        this.createView(mContext);
    }

    /**
     * @param mContext
     */
    private void createView(Context mContext) {
        dialog = new Dialog(mContext, R.style.default_dialog);
        View dialogView = View.inflate(mContext, R.layout.common_dialog, null);
        btnSubmit = (Button) dialogView.findViewById(R.id.btn_submit);
        btnSubmit.setText(this.buttonText);
        tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(this.title);
            tv_title.setVisibility(View.VISIBLE);
        } else {
            tv_title.setText("");
            tv_title.setVisibility(View.GONE);
        }
        tv_content = (TextView) dialogView.findViewById(R.id.tv_content);
        tv_content.setGravity(Gravity.LEFT);
        tv_content.setText(this.content);
        dialog.setContentView(dialogView);
        dialog.setCancelable(isCancelable);
        dialog.setCanceledOnTouchOutside(isCancelable);
    }

    /**
     * @param title
     * @param content
     * @return
     */
    public CommonDialog setContent(String title, String content) {
        if (TextUtils.isEmpty(title)) {
            tv_title.setVisibility(View.GONE);
        } else {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }
        if (TextUtils.isEmpty(content)) {
            tv_content.setVisibility(View.GONE);
        } else {
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText(content);
        }
        return this;
    }

    /**
     * 设置描述context的对齐方式
     *
     * @param gravity
     * @return Gravity.LEFT, Gravity.TOP, Gravity.RIGHT, Gravity.BOTTOM
     */
    public CommonDialog setContextGravity(int gravity) {
        tv_content.setGravity(gravity);
        return this;
    }

    /**
     * @param isCancelable
     * @return
     */
    public CommonDialog setCancelable(boolean isCancelable) {
        this.dialog.setCancelable(isCancelable);
        return this;
    }

    /**
     * @param buttonListener
     * @return
     */
    public CommonDialog setButtonListener(final OnCommonButtonListener buttonListener) {
        if (buttonListener == null) {
            return this;
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonListener.onSubmitListener(dialog);
            }
        });
        return this;
    }

    /**
     * @param listener
     * @return
     */
    public CommonDialog setOnOutsideListener(DialogInterface.OnCancelListener listener) {
        dialog.setOnCancelListener(listener);
        return this;
    }

    /**
     * 获取弹出框对象
     *
     * @return
     */
    public Dialog getDialog() {
        return dialog;
    }

    /**
     * 显示弹出窗口
     */
    public void onShow() {
        dialog.show();
    }

    /**
     * 隐藏弹出窗口
     */
    public void onDimess() {
        dialog.dismiss();
    }

    /**
     *
     */
    public interface OnCommonButtonListener {
        void onSubmitListener(Dialog obj);
    }
}