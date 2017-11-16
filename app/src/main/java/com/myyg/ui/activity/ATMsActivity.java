package com.myyg.ui.activity;

import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.UserModel;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.rey.material.widget.Button;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/5/30.
 */
public class ATMsActivity extends BaseActivity {
    private TextView tv_balance;

    private EditText edit_input_money, edit_input_username, edit_input_bankname,
            edit_input_branch, edit_input_bankno, edit_input_phone;

    private float reward = 0f;

    @Override
    public void initView() {
        setContentView(R.layout.activity_atms);
        this.tv_balance = (TextView) findViewById(R.id.tv_balance);
        this.edit_input_money = (EditText) findViewById(R.id.edit_input_money);
        this.edit_input_username = (EditText) findViewById(R.id.edit_input_username);
        this.edit_input_bankname = (EditText) findViewById(R.id.edit_input_bankname);
        this.edit_input_branch = (EditText) findViewById(R.id.edit_input_branch);
        this.edit_input_bankno = (EditText) findViewById(R.id.edit_input_bankno);
        this.edit_input_phone = (EditText) findViewById(R.id.edit_input_phone);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("提现申请");
        this.getMoney();
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
    private void getMoney() {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.USER_REWARD, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        return;
                    }
                    HashMap<String, Object> json = JSON.parseObject(message.getData(), HashMap.class);
                    reward = Float.parseFloat(String.valueOf(json.get("reward")));
                    String strMoney = MessageFormat.format("佣金余额：<font color=\"#C62334\">{0}</font> 元", String.valueOf(reward));
                    tv_balance.setText(Html.fromHtml(strMoney));
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {

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
    private void submit() {
        String money = this.edit_input_money.getText().toString().trim();
        String userName = this.edit_input_username.getText().toString().trim();
        String bankname = this.edit_input_bankname.getText().toString().trim();
        String branch = this.edit_input_branch.getText().toString().trim();
        String bankno = this.edit_input_bankno.getText().toString().trim();
        String phone = this.edit_input_phone.getText().toString().trim();
        if (this.reward < 100) {
            UIHelper.toastMessage(mContext, "您的佣金不足100元，不能提现");
            return;
        }
        if (money.length() < 3) {
            UIHelper.toastMessage(mContext, "大于100元才能提现");
            return;
        }
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(bankname) || TextUtils.isEmpty(branch) || TextUtils.isEmpty(bankno)) {
            UIHelper.toastMessage(this.mContext, "请正确输入信息");
            return;
        }
        if (phone.length() < 11) {
            UIHelper.toastMessage(mContext, "输入手机号不正确");
            return;
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("money", money);
        params.addBodyParameter("username", userName);
        params.addBodyParameter("bankname", bankname);
        params.addBodyParameter("branch", branch);
        params.addBodyParameter("bankno", bankno);
        params.addBodyParameter("phone", phone);
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_ADD_CASH, params, new RequestCallBack<String>() {
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
                    UIHelper.toastMessage(mContext, "提交成功");
                    setResult(RESULT_OK);
                    finish();
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
}
