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
import com.myyg.constant.SysKeys;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.UserModel;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;

/**
 * Created by Adminstrator on 2016/6/13.
 */
public class ModifiedNicknameActivity extends BaseActivity {

    private EditText et_username;

    private UserModel mUser = BaseApplication.getUser();

    @Override
    public void initView() {
        setContentView(R.layout.activity_modified_nickname);
        this.et_username = (EditText) findViewById(R.id.et_username);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("修改昵称");
        this.et_username.setText(this.mUser.getUsername());
        this.et_username.setSelection(this.mUser.getUsername().length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_modified_nickname, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tv_item_save_nickname:
                modifyName();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存用户昵称
     */
    private void modifyName() {
        String userName = this.et_username.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            UIHelper.toastMessage(this.mContext, "输入内容不能为空");
            return;
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("username", userName);
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_UPDATE_USERNAME, params, new RequestCallBack<String>() {
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
                    BaseApplication.sharedPut(SysKeys.SHARED_USER_USERNAME, userName);
                    UIHelper.toastMessage(mContext, "修改成功");
                    setResult(RESULT_OK);
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
            }
        });
    }
}
