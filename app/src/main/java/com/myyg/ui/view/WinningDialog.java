package com.myyg.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.myyg.R;
import com.myyg.base.BaseApplication;
import com.myyg.utils.CommonHelper;

import java.text.MessageFormat;

/**
 * Created by shiyuankao on 2016/8/11.
 */
public class WinningDialog extends Dialog {
    private Context mContext;
    private String title;
    private String qishu;

    private ImageView iv_close;

    private TextView tv_title;

    private TextView tv_qishu;

    private LinearLayout ll_content;

    public WinningDialog(Context context, String title, String qishu) {
        super(context, R.style.winning_dialog);
        this.setContentView(R.layout.dialog_winning);
        this.mContext = context;
        this.title = title;
        this.qishu = qishu;
        this.initView();
    }

    /**
     *
     */
    private void initView() {
        this.iv_close = (ImageView) this.findViewById(R.id.iv_close);
        this.tv_title = (TextView) this.findViewById(R.id.tv_title);
        this.tv_qishu = (TextView) this.findViewById(R.id.tv_qishu);
        this.tv_title.setText(this.title);
        this.tv_qishu.setText(MessageFormat.format("{0}期", this.qishu));
        this.iv_close.setOnClickListener(v -> dismiss());
        this.ll_content = (LinearLayout) this.findViewById(R.id.ll_content);
        if (BaseApplication.WINDOW_WIDTH >= 1080) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.ll_content.getLayoutParams();
            layoutParams.topMargin = CommonHelper.dp2px(this.mContext, 156);
            this.ll_content.setLayoutParams(layoutParams);
        }
    }
}
