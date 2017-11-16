package com.myyg.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.myyg.adapter.CommonAdapter;
import com.myyg.adapter.ViewHolder;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.ConfigKeys;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysEnums;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.db.DbHelper;
import com.myyg.model.MessageResult;
import com.myyg.model.PaymentModel;
import com.myyg.model.ShopCartModel;
import com.myyg.model.UserModel;
import com.myyg.utils.MyLog;
import com.myyg.utils.SharedHelper;
import com.myyg.utils.UIHelper;
import com.myyg.wxapi.WXPayEntryActivity;
import com.myyg.wxapi.model.WxPayModel;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettlementActivity extends BaseActivity {
    public final static String SHOPCART_CODE = "shopcart";
    private final static String TAG = SettlementActivity.class.getSimpleName();
    private ListView lv_shopcart;
    private CommonAdapter<ShopCartModel> adapter;
    private List<ShopCartModel> listData = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("0.00");
    private TextView tv_total_money;
    private TextView tv_score;
    private TextView tv_money;
    private UserModel user = BaseApplication.getUser();
    private ImageView iv_score_select, iv_money_select, iv_wx_select;
    private IWXAPI wxApi;
    private PaymentModel mPaymentModel;
    private PaymentBroadcastReceiver mPaymentBroadcastReceive;
    private TextView tv_score_title;
    private TextView tv_money_title;
    private float totalMoney;
    private RelativeLayout rl_score, rl_money, rl_wx_pay;
    private SysEnums.EnumOrderPaymentMode paymentMode;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_settlement);
        this.lv_shopcart = (ListView) this.findViewById(R.id.lv_shopcart);
        this.tv_total_money = (TextView) this.findViewById(R.id.tv_total_money);
        this.tv_score = (TextView) this.findViewById(R.id.tv_score);
        this.tv_money = (TextView) this.findViewById(R.id.tv_money);
        this.tv_score_title = (TextView) this.findViewById(R.id.tv_score_title);
        this.tv_money_title = (TextView) this.findViewById(R.id.tv_money_title);
        this.iv_score_select = (ImageView) this.findViewById(R.id.iv_score_select);
        this.iv_money_select = (ImageView) this.findViewById(R.id.iv_money_select);
        this.iv_wx_select = (ImageView) this.findViewById(R.id.iv_wx_select);
        this.rl_score = (RelativeLayout) this.findViewById(R.id.rl_score);
        this.rl_money = (RelativeLayout) this.findViewById(R.id.rl_money);
        this.rl_wx_pay = (RelativeLayout) this.findViewById(R.id.rl_wx_pay);
    }

    @Override
    public void initData() {
        String wxAppId = SharedHelper.getString(ConfigKeys.PARAMS_WX_APP_ID);
        this.wxApi = WXAPIFactory.createWXAPI(this, wxAppId, false);
        this.listData = (List<ShopCartModel>) this.getIntent().getSerializableExtra(SHOPCART_CODE);
    }

    @Override
    public void fillView() {
        this.setToolBar("结算支付");
        this.adapter = new CommonAdapter<ShopCartModel>(this.mContext, listData, R.layout.item_shopcart_payment) {

            @Override
            public void convert(ViewHolder helper, ShopCartModel item, int position) {
                helper.setText(R.id.tv_title, MessageFormat.format("(第{0}期){1}", item.getPeriods(), item.getGoodsName()));
                helper.setText(R.id.tv_money, Html.fromHtml(MessageFormat.format("<font color=\"#f95667\">{0}</font> 人次", df.format(item.getTotalMoney()))));
            }
        };
        this.lv_shopcart.setAdapter(this.adapter);
        totalMoney = DbHelper.getTotalMoney();
        this.tv_total_money.setText(Html.fromHtml(MessageFormat.format("合计：<font color=\"#c62435\">{0}</font> 元", df.format(totalMoney))));
        this.tv_score.setText(Html.fromHtml(MessageFormat.format("(云购币剩余：{0})", user.getScore())));
        this.tv_money.setText(MessageFormat.format("(账户余额：{0}元)", user.getMoney()));
        if (totalMoney > Float.parseFloat(this.user.getScore())) {
            this.iv_score_select.setEnabled(false);
        }
        if (totalMoney > Float.parseFloat(this.user.getMoney())) {
            this.iv_money_select.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_payment:
                this.confirmPayment();
                break;
        }
    }

    @Override
    public void bindListener() {
        super.bindListener();
        this.rl_score.setOnClickListener(v -> {
            this.iv_money_select.setImageResource(R.mipmap.ic_unchecked);
            this.iv_wx_select.setImageResource(R.mipmap.ic_unchecked);
            this.iv_score_select.setImageResource(R.mipmap.ic_checked);
            this.paymentMode = SysEnums.EnumOrderPaymentMode.Score;
            //this.tv_score_title.setText(Html.fromHtml(MessageFormat.format("云购币支付<font color=\"#f95667\">{0}</font>元", df.format(this.totalMoney))));
            //this.tv_money_title.setText("余额支付0.00元");
        });
        this.rl_money.setOnClickListener(v -> {
            this.iv_wx_select.setImageResource(R.mipmap.ic_unchecked);
            this.iv_score_select.setImageResource(R.mipmap.ic_unchecked);
            this.iv_money_select.setImageResource(R.mipmap.ic_checked);
            this.paymentMode = SysEnums.EnumOrderPaymentMode.Momey;
            //this.tv_money_title.setText(Html.fromHtml(MessageFormat.format("余额支付<font color=\"#f95667\">{0}</font>元", df.format(this.totalMoney))));
            //this.tv_score_title.setText("云购币支付0.00元");
        });
        this.rl_wx_pay.setOnClickListener(v -> {
            this.iv_money_select.setImageResource(R.mipmap.ic_unchecked);
            this.iv_score_select.setImageResource(R.mipmap.ic_unchecked);
            this.iv_wx_select.setImageResource(R.mipmap.ic_checked);
            this.paymentMode = SysEnums.EnumOrderPaymentMode.WxPayment;
            //this.tv_score_title.setText("云购币支付0.00元");
            //this.tv_money_title.setText("余额支付0.00元");
        });
    }

    /**
     * 确认支付
     */
    private void confirmPayment() {
        if (this.paymentMode == null) {
            UIHelper.toastMessage(this.mContext, "请选择付款方式");
            return;
        }
        List<HashMap<String, Object>> listHashMap = new ArrayList<>();
        for (ShopCartModel item : listData) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", item.getGoodsId());
            hashMap.put("shenyu", item.getTotalNumber() - item.getSurplusNumber());
            hashMap.put("num", item.getJoinNumber());
            hashMap.put("money", item.getPrice());
            listHashMap.add(hashMap);
        }
        boolean isWxPayment = this.paymentMode.getValue().equals(SysEnums.EnumOrderPaymentMode.WxPayment.getValue());
        if (isWxPayment) {
            this.wxPayment(listHashMap);
            return;
        }
        this.otherPayment(listHashMap);
    }

    /**
     * 余额支付或云购币支付
     *
     * @param listHashMap
     */
    private void otherPayment(List<HashMap<String, Object>> listHashMap) {
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("pay_type", this.paymentMode.getValue());
        params.addBodyParameter("cart_lists", JSON.toJSONString(listHashMap));
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_PAY_SUBMIT, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult result = MessageResult.parse(responseInfo.result);
                    if (result.getCode() != SysStatusCode.SUCCESS) {
                        MyLog.i(TAG, result.getMsg());
                        UIHelper.toastMessage(mContext, result.getMsg());
                        return;
                    }
                    DbHelper.clearShopCart();
                    loadUserInfo();
                    finish();
                    UIHelper.startActivity(mContext, PaymentResultActivity.class);
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyLog.i(TAG, s);
                UIHelper.hideLoading();
            }
        });
    }

    /**
     * 获取用户基本信息
     */
    private void loadUserInfo() {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.USER_GET_INFO, params, null);
    }

    /**
     * 微信支付
     *
     * @param listHashMap
     */
    private void wxPayment(List<HashMap<String, Object>> listHashMap) {
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("money", String.valueOf(this.totalMoney));
        params.addBodyParameter("pay_type", SysEnums.EnumPaymentChannel.WxPayment.getValue());
        params.addBodyParameter("extra", JSON.toJSONString(listHashMap));
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
                    wxPayment(mPaymentModel.getData());
                } catch (Exception ex) {
                    UIHelper.hideLoading();
                    MyLog.e(TAG, ex.getMessage());
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
    private void wxPayment(String paymentData) {
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
            new Handler().post(() -> {
                this.wxApi.sendReq(req);
            });
            this.mPaymentBroadcastReceive = new PaymentBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SysConstant.ACTION_MYYG_RECEIVE_WX_PAYMENT);
            this.registerReceiver(this.mPaymentBroadcastReceive, filter);
        } catch (Exception e) {
            UIHelper.hideLoading();
        } finally {

        }
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
                    UIHelper.toastMessage(mContext, "付款成功");
                    DbHelper.clearShopCart();
                    finish();
                    UIHelper.startActivity(mContext, PaymentResultActivity.class);
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
