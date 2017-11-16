package com.myyg.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

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
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysHtml;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.model.UserModel;
import com.myyg.utils.AppManager;
import com.myyg.utils.UIHelper;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MobileRegisterActivity extends BaseActivity {

    private EditText et_mobile, et_verifycode, et_password;

    private Button btn_send_code;

    private MyTimer timer;

    private CheckBox cb_agree;

    private Button btn_register;

    @Override
    public void initView() {
        setContentView(R.layout.activity_mobile_register);
        this.et_mobile = (EditText) this.findViewById(R.id.et_mobile);
        this.et_verifycode = (EditText) this.findViewById(R.id.et_verifycode);
        this.et_password = (EditText) this.findViewById(R.id.et_password);
        this.btn_send_code = (Button) this.findViewById(R.id.btn_send_code);
        this.cb_agree = (CheckBox) this.findViewById(R.id.cb_agree);
        this.btn_register = (Button) this.findViewById(R.id.btn_register);
    }

    @Override
    public void initData() {
        this.timer = new MyTimer(90000, 1000);
    }

    @Override
    public void fillView() {
        this.setToolBar("快速注册");
    }

    @Override
    public void bindListener() {
        this.cb_agree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.agree();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                AppManager.getAppManager().finishActivity();
                AppManager.getAppManager().finishActivity(LoginActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_code:
                this.sendVerifyCode();
                break;
            case R.id.btn_register:
                this.register();
                break;
            case R.id.tv_service:
                Bundle bundle = new Bundle();
                bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, SysHtml.HTML_SERVICE);
                UIHelper.startActivity(this.mContext, WebBrowseActivity.class, bundle);
                break;
        }
    }

    /**
     *
     */
    private void agree() {
        boolean isAgree = this.cb_agree.isChecked();
        if (!isAgree) {
            this.btn_register.setBackgroundResource(R.drawable.disable_button_bg);
            this.btn_register.setEnabled(false);
            return;
        }
        this.btn_register.setBackgroundResource(R.drawable.mobile_phone_register_button_bg);
        this.btn_register.setEnabled(true);
    }

    /**
     * 发送验证码
     */
    private void sendVerifyCode() {
        String mobile = this.et_mobile.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            UIHelper.toastMessage(this.mContext, getString(R.string.input_phone_num));
            return;
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("type", "register");
        http.send(HttpRequest.HttpMethod.POST, URLS.USERAUTH_SEND_CODE, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                timer.start();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() != SysStatusCode.SUCCESS) {
                    timer.cancel();
                    UIHelper.toastMessage(mContext, message.getMsg());
                    return;
                }
                UIHelper.toastMessage(mContext, getString(R.string.sms_success));
                String code = message.getData();
                if (!TextUtils.isEmpty(code)) {
                    UIHelper.toastMessage(mContext, "验证码为：" + code);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                timer.cancel();
            }
        });
    }

    /**
     * 注册
     */
    private void register() {
        String mobile = this.et_mobile.getText().toString();
        String password = this.et_password.getText().toString();
        String code = this.et_verifycode.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            UIHelper.toastMessage(this.mContext, getString(R.string.input_phone_num));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            UIHelper.toastMessage(this.mContext, getString(R.string.no_password_set));
            return;
        }
        if (password.length() <= 3) {
            UIHelper.toastMessage(mContext, getString(R.string.low_password_level));
            return;
        }
        if (TextUtils.isEmpty(code) || code.length() < 6) {
            UIHelper.toastMessage(this.mContext, getString(R.string.enter_a_valid));
            return;
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("code", code);
        params.addBodyParameter("password", password);
        http.send(HttpRequest.HttpMethod.POST, URLS.USERAUTH_REGITER, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, message.getMsg());
                    UIHelper.toastMessage(mContext, getString(R.string.register_fail));
                    return;
                }
                UIHelper.toastMessage(mContext, getString(R.string.register_success));
                finish();
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    /**
     *
     */
    class MyTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyTimer(long millisInFuture, long countDownInterval) {
            //参数依次为总时长,和计时的时间间隔
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_send_code.setEnabled(false);
            String strTip = MessageFormat.format("({0}s)", millisUntilFinished / 1000);
            btn_send_code.setText(strTip);
        }

        @Override
        public void onFinish() {
            btn_send_code.setText("获取验证码");
            btn_send_code.setEnabled(true);
        }
    }
}
