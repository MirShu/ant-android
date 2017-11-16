package com.myyg.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.myyg.R;
import com.myyg.constant.ConfigKeys;
import com.myyg.constant.SysConstant;
import com.myyg.utils.SharedHelper;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.text.MessageFormat;

/**
 * Created by JOHN on 2016/9/6.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = WXPayEntryActivity.class.getSimpleName();

    public static final String PARAM_IS_SUCCESS = "param_is_success";

    public static final String PARAM_ERROR_MSG = "param_error_msg";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        String wxAppId = SharedHelper.getString(ConfigKeys.PARAMS_WX_APP_ID);
        //wxAppId = getResources().getString(R.string.wx_app_id);
        api = WXAPIFactory.createWXAPI(this, wxAppId);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.setAction(SysConstant.ACTION_MYYG_RECEIVE_WX_PAYMENT);
        if (resp.errCode == 0) {
            bundle.putBoolean(PARAM_IS_SUCCESS, true);
            intent.putExtras(bundle);
            sendBroadcast(intent);
            this.finish();
            return;
        }
        // 用户取消支付
        if (resp.errCode == -2) {
            bundle.putBoolean(PARAM_IS_SUCCESS, false);
            bundle.putString(PARAM_ERROR_MSG, "取消支付");
            intent.putExtras(bundle);
            sendBroadcast(intent);
            this.finish();
            return;
        }
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            String errorMsg = MessageFormat.format("微信支付结果：{0};错误代码：{1}", resp.errStr, String.valueOf(resp.errCode));
            bundle.putBoolean(PARAM_IS_SUCCESS, false);
            bundle.putString(PARAM_ERROR_MSG, errorMsg);
            intent.putExtras(bundle);
            sendBroadcast(intent);
            this.finish();
        }
    }
}
