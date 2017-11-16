package com.myyg.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.myyg.R;
import com.myyg.base.BaseApplication;

/**
 * Created by JOHN on 2016/6/27.
 */
public class LoadingDialog extends Dialog {
    private Context mContext;

    public LoadingDialog(Context context) {
        super(context, R.style.loading_theme);
        this.mContext = context;
        this.initView();
    }

    /**
     *
     */
    private void initView() {
        this.setContentView(R.layout.loading_dialog);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        super.show();
        Window dialogWindow = getWindow(); //得到对话框
        //dialogWindow.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        //params.alpha = 0.8f;
        params.width = BaseApplication.WINDOW_WIDTH;
        params.height = BaseApplication.WINDOW_HEIGHT;
        dialogWindow.setAttributes(params);
    }
}
