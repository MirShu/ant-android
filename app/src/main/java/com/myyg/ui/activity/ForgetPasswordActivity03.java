package com.myyg.ui.activity;

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
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;

/**
 * Created by Administrator on 2016/6/1.
 */
public class ForgetPasswordActivity03 extends BaseActivity {
    private final static String TAG = ForgetPasswordActivity03.class.getSimpleName();
    public final static String FORGET_MOBILE = "forget_mobile";

    private EditText et_password;

    private String mobile;

    @Override
    public void initView() {
        setContentView(R.layout.activity_forget_password03);
        this.et_password = (EditText) this.findViewById(R.id.et_password);
    }

    @Override
    public void initData() {
        this.mobile = this.getIntent().getStringExtra(FORGET_MOBILE);
    }

    @Override
    public void fillView() {
        this.setToolBar("找回密码");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                this.submit();
                break;
        }
    }

    /**
     *
     */
    private void submit() {
        String password = this.et_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            UIHelper.toastMessage(this.mContext, "请正确输入新密码");
            return;
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("password", password);
        http.send(HttpRequest.HttpMethod.POST, URLS.USERAUTH_CODE_VALID, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, result.getMsg());
                    return;
                }
                UIHelper.toastMessage(mContext, "密码重置成功");
                closeActivity();
            }

            @Override
            public void onFailure(HttpException e, String s) {
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
                this.closeActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    private void closeActivity() {
        AppManager.getAppManager().finishActivity();
        AppManager.getAppManager().finishActivity(ForgetPasswordActivity01.class);
        AppManager.getAppManager().finishActivity(ForgetPasswordActivity02.class);
        AppManager.getAppManager().finishActivity(LoginActivity.class);
    }
}
