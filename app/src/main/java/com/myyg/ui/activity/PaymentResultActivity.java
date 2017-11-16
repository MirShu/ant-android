package com.myyg.ui.activity;

import android.content.Intent;
import android.view.View;

import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.constant.SysConstant;
import com.myyg.utils.UIHelper;

public class PaymentResultActivity extends BaseActivity {

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_payment_result);
    }

    @Override
    public void initData() {
        Intent intent = new Intent();
        intent.setAction(SysConstant.ACTION_MYYG_RECEIVE_HOME);
        this.sendBroadcast(intent);
    }

    @Override
    public void fillView() {
        this.setToolBar("云购结果");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_see_record:
                UIHelper.startActivity(this.mContext, BiddingRecordActivity.class);
                break;
            case R.id.btn_continue_shop:
                setResult(RESULT_OK);
                break;
        }
        this.finish();
    }
}
