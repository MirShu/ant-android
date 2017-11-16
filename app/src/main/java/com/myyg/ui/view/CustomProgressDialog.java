package com.myyg.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.myyg.R;

public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context, String strMessage) {
        this(context, R.style.customProgressDialog, strMessage);
    }

    public CustomProgressDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.view_dialog_progress);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        RelativeLayout rl_wrap = (RelativeLayout) findViewById(R.id.rl_wrap);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_wrap.getLayoutParams();
//        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        params.width = (int) (dm.widthPixels * 0.8);
//        rl_wrap.setLayoutParams(params);
        TextView tvMsg = (TextView) this.findViewById(R.id.tv_message);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            dismiss();
        }
    }
}
