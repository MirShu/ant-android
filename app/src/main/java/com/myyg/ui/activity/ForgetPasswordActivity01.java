package com.myyg.ui.activity;

import android.os.Bundle;
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
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.utils.AppManager;
import com.myyg.utils.UIHelper;
import com.rey.material.widget.Button;

/**
 * Created by Administrator on 2016/5/30.
 */
public class ForgetPasswordActivity01 extends BaseActivity {
    private final static String TAG = ForgetPasswordActivity01.class.getSimpleName();

    private EditText et_mobile;

    private Button btn_next;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_forget_password01);
        this.et_mobile = (EditText) this.findViewById(R.id.et_mobile);
        this.btn_next = (Button) this.findViewById(R.id.btn_next);
    }

    @Override
    public void initData() {

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
                this.verifyMobile();
                break;
        }
    }

    private void verifyMobile() {
        String mobile = this.et_mobile.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            UIHelper.toastMessage(this.mContext, "请正确输入手机号码");
            return;
        }
        this.btn_next.setEnabled(false);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("mobile", mobile);
        http.send(HttpRequest.HttpMethod.POST, URLS.USERAUTH_ACCOUNT_VALID, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    btn_next.setEnabled(true);
                    UIHelper.toastMessage(mContext, result.getMsg());
                    return;
                }
                sendVerifyCode(mobile);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                btn_next.setEnabled(true);
            }
        });
    }

    /**
     * 发送验证码
     */
    private void sendVerifyCode(String mobile) {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("type", "reset");
        http.send(HttpRequest.HttpMethod.POST, URLS.USERAUTH_SEND_CODE, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                btn_next.setEnabled(true);
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, result.getMsg());
                    return;
                }
                UIHelper.toastMessage(mContext, "发送成功");
                String code = result.getData();
                if (!TextUtils.isEmpty(code)) {
                    UIHelper.toastMessage(mContext, "验证码为：" + code);
                }
                Bundle bundle = new Bundle();
                bundle.putString(ForgetPasswordActivity02.FORGET_MOBILE, mobile);
                UIHelper.startActivity(mContext, ForgetPasswordActivity02.class, bundle);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                btn_next.setEnabled(true);
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
                AppManager.getAppManager().finishActivity(LoginActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
