package com.myyg.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
import com.myyg.constant.SysKeys;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.rey.material.widget.Button;

import java.text.MessageFormat;

public class ModifyMobileActivity extends BaseActivity {
    private static final String TAG = ModifyMobileActivity.class.getSimpleName();

    private EditText et_mobile, et_verifycode;

    private Button btn_send_code;

    private MyTimer timer;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_modify_mobile);
        this.et_mobile = (EditText) this.findViewById(R.id.et_mobile);
        this.et_verifycode = (EditText) this.findViewById(R.id.et_verifycode);
        this.btn_send_code = (Button) this.findViewById(R.id.btn_send_code);
    }

    @Override
    public void initData() {
        this.timer = new MyTimer(90000, 1000);
    }

    @Override
    public void fillView() {
        this.setToolBar("修改手机号");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_common_text, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                this.save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_code:
                this.sendVerifyCode();
                break;
        }
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
        params.addBodyParameter("type", "modify");
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
                    timer.onFinish();
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
     *
     */
    private void save() {
        String mobile = this.et_mobile.getText().toString().trim();
        String verifyCode = this.et_verifycode.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            UIHelper.toastMessage(this.mContext, "请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            UIHelper.toastMessage(this.mContext, "请输入验证码");
            return;
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("code", verifyCode);
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_MODIFY_MOBILE, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                UIHelper.showLoading(mContext);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        UIHelper.toastMessage(mContext, message.getMsg());
                        return;
                    }
                    UIHelper.toastMessage(mContext, "修改成功");
                    BaseApplication.sharedPut(SysKeys.SHARED_USER_MOBILE, mobile);
                    finish();
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {
                    UIHelper.hideLoading();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
                MyLog.e(TAG, s);
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
