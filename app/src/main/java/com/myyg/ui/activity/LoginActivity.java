package com.myyg.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysEnums;
import com.myyg.constant.SysHtml;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.listener.SimpleUiListener;
import com.myyg.model.AuthLoginModel;
import com.myyg.model.MessageResult;
import com.myyg.model.UserModel;
import com.myyg.utils.AppManager;
import com.myyg.utils.ClickFilter;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.utils.WxHelper;
import com.myyg.wxapi.WXEntryActivity;
import com.myyg.wxapi.model.WxUserModel;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity {
    private EditText et_username;
    private EditText et_password;

    private Tencent mTencent;

    private QQLoginListener qqLoginListener;

    private LoginBroadcastReceiver mBroadcastReceiver;

    private AuthLoginModel authLoginModel;

    private int tabIndex = 0;

    @Override
    public void initView() {
        setContentView(R.layout.activity_login);
        this.et_username = (EditText) this.findViewById(R.id.et_username);
        this.et_password = (EditText) this.findViewById(R.id.et_password);
    }

    @Override
    public void initData() {
        this.authLoginModel = new AuthLoginModel();
        this.qqLoginListener = new QQLoginListener();
        this.mTencent = Tencent.createInstance(this.getString(R.string.qq_app_key), this.getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(SysConstant.ACTION_MYYG_RECEIVE_WX_LOGIN);
        if (this.mBroadcastReceiver == null) {
            this.mBroadcastReceiver = new LoginBroadcastReceiver();
        }
        this.registerReceiver(mBroadcastReceiver, filter);
        this.tabIndex = this.getIntent().getIntExtra(MainActivity.MAIN_TAB_INDEX, this.tabIndex);
    }

    @Override
    public void fillView() {
        this.setToolBar("登录");
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                UIHelper.startActivity(this.mContext, MobileRegisterActivity.class);
                break;
            case R.id.tv_forget_password:
                UIHelper.startActivity(this.mContext, ForgetPasswordActivity01.class);
                break;
            case R.id.btn_login:
                this.login();
                break;
            case R.id.tv_about:
                Bundle bundle = new Bundle();
                bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, SysHtml.HTML_ABOUT_US);
                UIHelper.startActivity(this.mContext, WebBrowseActivity.class, bundle);
                break;
            case R.id.tv_privacy_policy:
                bundle = new Bundle();
                bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, SysHtml.HTML_PRIVACY_POLICY);
                UIHelper.startActivity(this.mContext, WebBrowseActivity.class, bundle);
                break;
            case R.id.ll_qq:
                if (ClickFilter.filter()) {
                    return;
                }
                this.qqLogin();
                break;
            case R.id.ll_wx:
                if (ClickFilter.filter()) {
                    return;
                }
                WxHelper.getInstance(this.mContext).wxLogin();
                break;
        }
    }

    /**
     * 用户登录
     */
    private void login() {
        String userName = this.et_username.getText().toString().trim();
        String password = this.et_password.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            UIHelper.toastMessage(this.mContext, "请正确输入登录信息");
            return;
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("username", userName);
        params.addBodyParameter("password", password);
        http.send(HttpRequest.HttpMethod.POST, URLS.EXTRA_LOGIN, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                UIHelper.showLoading(mContext);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        UIHelper.toastMessage(mContext, "用户名或密码不正确");
                        return;
                    }
                    UserModel user = UserModel.parse(message.getData());
                    loginComplete(user);
                } catch (Exception ex) {

                } finally {
                    UIHelper.hideLoading();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
            }
        });
    }

    /**
     * 其它登录
     */
    private void otherLogin() {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("open_id", this.authLoginModel.getOpenId());
        params.addBodyParameter("avatar", this.authLoginModel.getAvatar());
        params.addBodyParameter("nickname", this.authLoginModel.getNickName());
        params.addBodyParameter("login_type", this.authLoginModel.getAuthLoginType().getValue() + "");
        http.send(HttpRequest.HttpMethod.POST, URLS.USERAUTH_OAUTH_LOGIN, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                UIHelper.showLoading(mContext, "正在登录,请稍等...");
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        UIHelper.toastMessage(mContext, message.getMsg());
                        return;
                    }
                    UserModel user = UserModel.parse(message.getData());
                    loginComplete(user);
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {
                    UIHelper.hideLoading();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
            }
        });
    }

    /**
     * 登录完成
     *
     * @param user
     */
    private void loginComplete(UserModel user) {
        JPushInterface.setAlias(this.mContext, user.getUid(), null);
        BaseApplication.sharedLogin(user);
        Intent intent = new Intent();
        intent.putExtra(MainActivity.MAIN_TAB_INDEX, this.tabIndex);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * QQ登录
     */
    private void qqLogin() {
        //this.mTencent.login(this.mContext, "get_user_info", this.qqLoginListener);
        this.mTencent.login(this.mContext, "all", this.qqLoginListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.mTencent.onActivityResultData(requestCode, resultCode, data, this.qqLoginListener);
    }

    /**
     * 获取用户基本信息
     */
    private void getUserInfo() {
        UserInfo user = new UserInfo(this.mContext, this.mTencent.getQQToken());
        user.getUserInfo(new SimpleUiListener() {
            @Override
            public void onComplete(Object o) {
                try {
                    MyLog.e(TAG, o.toString());
                    org.json.JSONObject json = (org.json.JSONObject) o;
                    authLoginModel.setAuthLoginType(SysEnums.EnumAuthLogin.QQ);
                    authLoginModel.setNickName(json.getString("nickname"));
                    authLoginModel.setAvatar(json.getString("figureurl_qq_2"));
                    otherLogin();
                } catch (Exception e) {
                    MyLog.e(TAG, e.getMessage());
                }
            }
        });
    }

    /**
     * QQ登录
     */
    public class QQLoginListener extends SimpleUiListener {
        @Override
        public void onComplete(Object o) {
            try {
                MyLog.i(TAG, "onComplete");
                MyLog.i(TAG, o.toString());
                org.json.JSONObject json = (org.json.JSONObject) o;
                initOpenidAndToken(json);
                getUserInfo();
            } catch (Exception ex) {
                MyLog.e(TAG, ex.getMessage());
            }
        }
    }

    /**
     * 初始Token信息
     *
     * @param jsonObject
     */
    public void initOpenidAndToken(org.json.JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                this.mTencent.setAccessToken(token, expires);
                this.mTencent.setOpenId(openId);
            }
            this.authLoginModel.setOpenId(openId);
        } catch (Exception e) {
            MyLog.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBroadcastReceiver != null) {
            this.unregisterReceiver(this.mBroadcastReceiver);
        }
    }

    /**
     * 定义广播
     */
    public class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 微信登录
            if (action.equals(SysConstant.ACTION_MYYG_RECEIVE_WX_LOGIN)) {
                WxUserModel wxUserModel = (WxUserModel) intent.getSerializableExtra(WXEntryActivity.WX_USER_INFO);
                MyLog.i(TAG, wxUserModel.getNickname());
                authLoginModel.setAuthLoginType(SysEnums.EnumAuthLogin.Wx);
                authLoginModel.setNickName(wxUserModel.getNickname());
                authLoginModel.setOpenId(wxUserModel.getUnionid());
                authLoginModel.setAvatar(wxUserModel.getHeadimgurl());
                otherLogin();
            }
        }
    }
}
