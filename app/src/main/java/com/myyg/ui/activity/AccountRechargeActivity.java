package com.myyg.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.ConfigKeys;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysEnums;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.PaymentModel;
import com.myyg.model.UserModel;
import com.myyg.utils.MyLog;
import com.myyg.utils.SharedHelper;
import com.myyg.utils.UIHelper;
import com.myyg.wxapi.WXPayEntryActivity;
import com.myyg.wxapi.model.WxPayModel;
import com.rey.material.widget.Button;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.text.MessageFormat;

/**
 * Created by Administrator on 2016/6/1.
 */
public class AccountRechargeActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_twenty_yuan, tv_fifty_yuan, tv_hundred_yuan,
            tv_two_hundred_yuan, tv_five_hundred_yuan, tv_money_num;

    private EditText et_money;

    private RadioGroup rg_payment_method;

    private RadioButton rb_wx_payment, rb_union_payment;

    private float money = 0;

    private PaymentBroadcastReceiver mPaymentBroadcastReceive;

    private IWXAPI wxApi;

    private PaymentModel mPaymentModel;

    private SysEnums.EnumPaymentChannel mPaymentChannel;

    @Override
    public void initView() {
        setContentView(R.layout.activity_account_recharge);
        this.tv_twenty_yuan = (TextView) findViewById(R.id.tv_twenty_yuan);
        this.tv_fifty_yuan = (TextView) findViewById(R.id.tv_fifty_yuan);
        this.tv_hundred_yuan = (TextView) findViewById(R.id.tv_hundred_yuan);
        this.tv_two_hundred_yuan = (TextView) findViewById(R.id.tv_two_hundred_yuan);
        this.tv_five_hundred_yuan = (TextView) findViewById(R.id.tv_five_hundred_yuan);
        this.tv_money_num = (TextView) findViewById(R.id.tv_money_num);
        this.et_money = (EditText) findViewById(R.id.et_money);
        this.rg_payment_method = (RadioGroup) findViewById(R.id.rg_payment_method);
        this.rb_wx_payment = (RadioButton) findViewById(R.id.rb_wx_payment);
        this.rb_union_payment = (RadioButton) findViewById(R.id.rb_union_payment);
    }

    @Override
    public void initData() {
        String wxAppId = SharedHelper.getString(ConfigKeys.PARAMS_WX_APP_ID);
        wxApi = WXAPIFactory.createWXAPI(this, wxAppId);
    }

    @Override
    public void fillView() {
        setToolBar("账户充值");
        UserModel user = BaseApplication.getUser();
        this.tv_money_num.setText(user.getMoney());
    }

    @Override
    public void bindListener() {
        super.bindListener();
        this.rg_payment_method.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_wx_payment:
                    this.mPaymentChannel = SysEnums.EnumPaymentChannel.WxPayment;
                    break;
                case R.id.rb_union_payment:
                    this.mPaymentChannel = SysEnums.EnumPaymentChannel.UnionPayment;
                    break;
            }
        });
        et_money.setOnTouchListener((v, event) -> {
            tv_twenty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
            tv_fifty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
            tv_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
            tv_two_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
            tv_five_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
            et_money.setBackgroundResource(R.drawable.money_charge_tv_choice_bg);
            return false;
        });
        tv_twenty_yuan.setOnClickListener(this);
        tv_fifty_yuan.setOnClickListener(this);
        tv_hundred_yuan.setOnClickListener(this);
        tv_two_hundred_yuan.setOnClickListener(this);
        tv_five_hundred_yuan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_twenty_yuan:
                tv_twenty_yuan.setBackgroundResource(R.drawable.money_charge_tv_choice_bg);
                tv_fifty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_two_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_five_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                et_money.setBackgroundResource(R.drawable.money_charge_tv_bg);
                et_money.setText("");
                closeSoftKeyboard();
                this.money = 20;
                break;
            case R.id.tv_fifty_yuan:
                tv_twenty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_fifty_yuan.setBackgroundResource(R.drawable.money_charge_tv_choice_bg);
                tv_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_two_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_five_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                et_money.setBackgroundResource(R.drawable.money_charge_tv_bg);
                et_money.setText("");
                closeSoftKeyboard();
                this.money = 50;
                break;
            case R.id.tv_hundred_yuan:
                tv_twenty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_fifty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_choice_bg);
                tv_two_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_five_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                et_money.setBackgroundResource(R.drawable.money_charge_tv_bg);
                et_money.setText("");
                closeSoftKeyboard();
                this.money = 100;
                break;
            case R.id.tv_two_hundred_yuan:
                tv_twenty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_fifty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_two_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_choice_bg);
                tv_five_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                et_money.setBackgroundResource(R.drawable.money_charge_tv_bg);
                et_money.setText("");
                closeSoftKeyboard();
                this.money = 200;
                break;
            case R.id.tv_five_hundred_yuan:
                tv_twenty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_fifty_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_two_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_bg);
                tv_five_hundred_yuan.setBackgroundResource(R.drawable.money_charge_tv_choice_bg);
                et_money.setBackgroundResource(R.drawable.money_charge_tv_bg);
                et_money.setText("");
                closeSoftKeyboard();
                this.money = 500;
                break;
            case R.id.btn_recharge:
                this.recharge();
                break;
        }
    }

    /**
     * 充值
     */
    private void recharge() {
        if (this.money <= 0) {
            String strMoney = this.et_money.getText().toString().trim();
            if (!TextUtils.isEmpty(strMoney)) {
                this.money = Float.parseFloat(strMoney);
            }
        }
        if (this.money <= 0) {
            UIHelper.toastMessage(this.mContext, "请正确输入充值金额");
            return;
        }
        if (this.mPaymentChannel == null) {
            UIHelper.toastMessage(this.mContext, "请选择充值渠道");
            return;
        }
        if (this.mPaymentChannel == SysEnums.EnumPaymentChannel.UnionPayment) {
            UIHelper.toastMessage(this.mContext, "暂不支持银联支付");
            return;
        }
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("money", String.valueOf(this.money));
        params.addBodyParameter("pay_type", this.mPaymentChannel.getValue());
        params.addBodyParameter("extra", "");
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_RECHARGE, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                UIHelper.showLoading(mContext);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult result = MessageResult.parse(responseInfo.result);
                    if (result.getCode() != SysStatusCode.SUCCESS) {
                        UIHelper.toastMessage(mContext, result.getMsg());
                        return;
                    }
                    mPaymentModel = JSON.parseObject(result.getData(), PaymentModel.class);
                    wxPay(mPaymentModel.getData());
                } catch (Exception ex) {
                    UIHelper.hideLoading();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyLog.e(TAG, s);
                UIHelper.hideLoading();
            }
        });
    }

    /**
     * 微信支付
     */
    private void wxPay(String paymentData) {
        try {
            UIHelper.showLoading(this.mContext);
            WxPayModel wxPay = JSON.parseObject(paymentData, WxPayModel.class);
            PayReq req = new PayReq();
            if (!wxApi.isWXAppInstalled()) {
                UIHelper.hideLoading();
                UIHelper.toastMessage(this.mContext, "请先安装微信App");
                return;
            }
            if (!wxApi.isWXAppSupportAPI()) {
                UIHelper.hideLoading();
                UIHelper.toastMessage(this.mContext, "当前版本不支持支付功能");
                return;
            }
            req.appId = wxPay.getAppid();
            req.partnerId = wxPay.getPartnerId();
            req.prepayId = wxPay.getPrepayid();
            req.packageValue = wxPay.getPackage();
            req.nonceStr = wxPay.getNoncestr();
            req.timeStamp = wxPay.getTimestamp();
            req.sign = wxPay.getSign();
            this.wxApi.sendReq(req);
            this.mPaymentBroadcastReceive = new PaymentBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SysConstant.ACTION_MYYG_RECEIVE_WX_PAYMENT);
            this.registerReceiver(this.mPaymentBroadcastReceive, filter);
        } catch (Exception e) {
            UIHelper.hideLoading();
        } finally {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mPaymentBroadcastReceive != null) {
            this.unregisterReceiver(this.mPaymentBroadcastReceive);
        }
    }

    /**
     * 关闭输入软键方法
     */
    private void closeSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(AccountRechargeActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     *
     */
    public class PaymentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // 支付完成调用服务器验证
            if (action.equals(SysConstant.ACTION_MYYG_RECEIVE_WX_PAYMENT)) {
                UIHelper.hideLoading();
                // 判断客户端是否支付成功
                boolean isSuccess = extras.getBoolean(WXPayEntryActivity.PARAM_IS_SUCCESS);
                if (isSuccess) {
                    completeVerify();
                    return;
                }
                String errorMsg = extras.getString(WXPayEntryActivity.PARAM_ERROR_MSG);
                if (errorMsg != null && errorMsg.length() > 0) {
                    UIHelper.toastMessage(mContext, errorMsg);
                } else {
                    UIHelper.toastMessage(mContext, "支付失败");
                }
            }
        }
    }

    /**
     * 支付完成验证
     */
    public void completeVerify() {
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("orderid", this.mPaymentModel.getOrderid());
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_PAY_VERIFY, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult result = MessageResult.parse(responseInfo.result);
                    if (result.getCode() != SysStatusCode.SUCCESS) {
                        UIHelper.toastMessage(mContext, result.getMsg());
                        return;
                    }
                    UIHelper.toastMessage(mContext, "充值成功");
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyLog.e(TAG, s);
            }
        });
    }
}
