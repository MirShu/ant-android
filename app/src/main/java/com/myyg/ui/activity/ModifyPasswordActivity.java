package com.myyg.ui.activity;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;

public class ModifyPasswordActivity extends BaseActivity {
    private static final String TAG = ModifyPasswordActivity.class.getSimpleName();
    private EditText et_password;
    private EditText et_confirm_password;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_modify_password);
        this.et_password = (EditText) this.findViewById(R.id.et_password);
        this.et_confirm_password = (EditText) this.findViewById(R.id.et_confirm_password);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("修改密码");
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

    /**
     *
     */
    private void save() {
        String password = this.et_password.getText().toString().trim();
        String confirmPassword = this.et_confirm_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            UIHelper.toastMessage(this.mContext, "请输入新密码");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            UIHelper.toastMessage(this.mContext, "请输入确认密码");
            return;
        }
        if (!password.equals(confirmPassword)) {
            UIHelper.toastMessage(this.mContext, "两次输入的密码不一致");
            return;
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("password", password);
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_CHANGE_PASSWORD, params, new RequestCallBack<String>() {
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
}
