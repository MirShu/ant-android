package com.myyg.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.utils.AppManager;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.rey.material.widget.Button;

import org.w3c.dom.Text;

import java.text.MessageFormat;

/**
 * Created by Administrator on 2016/6/1.
 */
public class ForgetPasswordActivity02 extends BaseActivity {
    private final static String TAG = ForgetPasswordActivity02.class.getSimpleName();
    public final static String FORGET_MOBILE = "forget_mobile";

    private EditText et_verifycode;

    private String mobile;

    private MyTimer timer;

    private TextView tv_tips;

    private Button btn_reset_send;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_forget_password02);
        this.et_verifycode = (EditText) this.findViewById(R.id.et_verifycode);
        this.tv_tips = (TextView) this.findViewById(R.id.tv_tips);
        this.btn_reset_send = (Button) this.findViewById(R.id.btn_reset_send);
    }

    @Override
    public void initData() {
        this.mobile = this.getIntent().getStringExtra(FORGET_MOBILE);
        this.timer = new MyTimer(90000, 1000);
        this.timer.start();
    }

    @Override
    public void fillView() {
        this.setToolBar("找回密码");
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_next:
                this.codeVerify();
                break;
            case R.id.btn_reset_send:
                this.sendVerifyCode();
                break;
        }
    }

    /**
     * 验证码验证
     */
    private void codeVerify() {
        String code = this.et_verifycode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            UIHelper.toastMessage(this.mContext, "请正确输入验证码");
            return;
        }
        btn_reset_send.setEnabled(false);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("code", code);
        http.send(HttpRequest.HttpMethod.POST, URLS.USERAUTH_CODE_VALID, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, result.getMsg());
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(ForgetPasswordActivity03.FORGET_MOBILE, mobile);
                UIHelper.startActivity(mContext, ForgetPasswordActivity03.class, bundle);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                btn_reset_send.setEnabled(true);
                MyLog.e(TAG, s);
            }
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
                AppManager.getAppManager().finishActivity(ForgetPasswordActivity01.class);
                AppManager.getAppManager().finishActivity(LoginActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 发送验证码
     */
    private void sendVerifyCode() {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("type", "reset");
        http.send(HttpRequest.HttpMethod.POST, URLS.USERAUTH_SEND_CODE, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, result.getMsg());
                    return;
                }
                UIHelper.toastMessage(mContext, "发送成功");
                timer.start();
                String code = result.getData();
                if (!TextUtils.isEmpty(code)) {
                    UIHelper.toastMessage(mContext, code);
                }
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
            btn_reset_send.setEnabled(false);
            String strTip = MessageFormat.format("如未收到验证短信，请在{0}s秒后点击重新发送。", millisUntilFinished / 1000);
            tv_tips.setText(strTip);
        }

        @Override
        public void onFinish() {
            tv_tips.setText("如未收到验证短信，请在90s秒后点击重新发送。");
            btn_reset_send.setEnabled(true);
        }
    }
}
