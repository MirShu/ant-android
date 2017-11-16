package com.myyg.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myyg.R;

/**
 * Created by JOHN on 2015/11/16.
 */
public class DoubleButtonDialog {
    private Dialog dialog;
    private Button btn_left;
    private Button btn_right;
    private TextView tv_title;
    private TextView tv_content;
    private LinearLayout ll_button;
    private String leftText = "取消";
    private String rightText = "确定";
    private String title;
    private String content;

    /**
     * @param mContext
     */
    public DoubleButtonDialog(Context mContext) {
        this.leftText = "取消";
        this.rightText = "确定";
        this.createView(mContext);
    }

    /**
     * @param mContext
     * @param content
     */
    public DoubleButtonDialog(Context mContext, String content, OnButtonListener buttonListener) {
        this.leftText = "取消";
        this.rightText = "确定";
        this.content = content;
        this.createView(mContext);
        this.setButtonListener(buttonListener);
    }

    /**
     * @param mContext
     * @param title
     * @param content
     */
    public DoubleButtonDialog(Context mContext, String title, String content) {
        this.leftText = "取消";
        this.rightText = "确定";
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
    public DoubleButtonDialog(Context mContext, String title, String content, OnButtonListener buttonListener) {
        this.leftText = "取消";
        this.rightText = "确定";
        this.title = title;
        this.content = content;
        this.createView(mContext);
        this.setButtonListener(buttonListener);
    }

    /**
     * @param mContext
     * @param leftText
     * @param rightText
     * @param title
     * @param content
     */
    public DoubleButtonDialog(Context mContext, String leftText, String rightText, String title, String content) {
        this.leftText = leftText;
        this.rightText = rightText;
        this.title = title;
        this.content = content;
        this.createView(mContext);
    }

    /**
     * @param mContext
     */
    private void createView(Context mContext) {
        dialog = new Dialog(mContext, R.style.default_dialog);
        View dialogView = View.inflate(mContext, R.layout.common_dialog_double_button, null);
        ll_button = (LinearLayout) dialogView.findViewById(R.id.ll_button);
        btn_left = (Button) dialogView.findViewById(R.id.btn_left);
        btn_left.setText(this.leftText);
        btn_right = (Button) dialogView.findViewById(R.id.btn_right);
        btn_right.setText(this.rightText);
        tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(this.title);
            tv_title.setVisibility(View.VISIBLE);
        } else {
            tv_title.setText("");
            tv_title.setVisibility(View.GONE);
        }
        tv_content = (TextView) dialogView.findViewById(R.id.tv_content);
        tv_content.setGravity(Gravity.CENTER);
        tv_content.setText(this.content);
        dialog.setContentView(dialogView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    /**
     * @param title
     * @param content
     * @return
     */
    public DoubleButtonDialog setContent(String title, String content) {
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
    public DoubleButtonDialog setContextGravity(int gravity) {
        tv_content.setGravity(gravity);
        return this;
    }

    /**
     * 2个按钮
     *
     * @param leftText
     * @param rightText
     * @return
     */
    public DoubleButtonDialog setButtonText(String leftText, String rightText) {
        ll_button.setVisibility(View.VISIBLE);
        btn_left.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.VISIBLE);
        btn_left.setText(leftText);
        btn_right.setText(rightText);
        return this;
    }

    /**
     * @param isCancelable
     * @return
     */
    public DoubleButtonDialog setCancelable(boolean isCancelable) {
        this.dialog.setCancelable(isCancelable);
        return this;
    }

    /**
     * @param buttonListener
     * @return
     */
    public DoubleButtonDialog setButtonListener(final OnButtonListener buttonListener) {
        if (buttonListener == null) {
            return this;
        }
        btn_left.setOnClickListener(v -> buttonListener.onLeftListener(dialog));
        btn_right.setOnClickListener(v -> buttonListener.onRightListener(dialog));
        return this;
    }

    /**
     * @param listener
     * @return
     */
    public DoubleButtonDialog setOnOutsideListener(DialogInterface.OnCancelListener listener) {
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
    public interface OnButtonListener {
        void onLeftListener(Dialog obj);

        void onRightListener(Dialog obj);
    }
}