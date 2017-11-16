package com.myyg.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.myyg.R;
import com.myyg.utils.UIHelper;

/**
 * Created by JOHN on 2016/10/9.
 */

public class RechargeDialog extends Dialog {
    private EditText et_reward;
    private Button btn_cancel;
    private Button btn_ok;
    private float total_reward;
    private OnRewardListener listener;
    private Context mContext;

    /**
     * @param context
     * @param total_reward
     */
    public RechargeDialog(Context context, float total_reward) {
        super(context, R.style.default_dialog);
        this.setContentView(R.layout.dialog_recharge);
        this.mContext = context;
        this.total_reward = total_reward;
        this.initView();
    }

    /**
     *
     */
    private void initView() {
        this.et_reward = (EditText) this.findViewById(R.id.et_reward);
        this.btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        this.btn_ok = (Button) this.findViewById(R.id.btn_ok);
        this.et_reward.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        this.btn_cancel.setOnClickListener(v -> {
            this.dismiss();
        });
        this.btn_ok.setOnClickListener(v -> {
            String strReward = this.et_reward.getText().toString().trim();
            if (TextUtils.isEmpty(strReward)) {
                UIHelper.toastMessage(this.mContext, "请正确输入充值金额");
                return;
            }
            int reward = Integer.parseInt(strReward);
            if (reward <= 0 || reward > total_reward) {
                UIHelper.toastMessage(this.mContext, "请正确输入充值金额");
                return;
            }
            this.dismiss();
            if (this.listener != null) {
                this.listener.onReward(reward);
            }
        });
    }

    public void setListener(OnRewardListener listener) {
        this.listener = listener;
    }

    public interface OnRewardListener {
        void onReward(int reward);
    }
}
