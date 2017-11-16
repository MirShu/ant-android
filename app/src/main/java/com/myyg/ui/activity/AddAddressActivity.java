package com.myyg.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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
import com.myyg.db.DbHelper;
import com.myyg.model.AreaModel;
import com.myyg.model.MessageResult;
import com.myyg.utils.UIHelper;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/5/27.
 */
public class AddAddressActivity extends BaseActivity {

    private EditText edit_input_people, edit_input_phone, edit_input_address;

    private TextView text_sheng, text_shi, text_xian;

    private List<AreaModel> listProvince;

    private List<AreaModel> listCity;

    private List<AreaModel> listCounty;

    private AreaModel province;

    private AreaModel city;

    private AreaModel county;

    private CheckBox cb_default;

    @Override
    public void initView() {
        setContentView(R.layout.activity_add_address);
        this.edit_input_people = (EditText) findViewById(R.id.edit_input_people);
        this.edit_input_phone = (EditText) findViewById(R.id.edit_input_phone);
        this.edit_input_address = (EditText) findViewById(R.id.edit_input_address);
        this.text_sheng = (TextView) findViewById(R.id.text_sheng);
        this.text_shi = (TextView) findViewById(R.id.text_shi);
        this.text_xian = (TextView) findViewById(R.id.text_xian);
        this.cb_default = (CheckBox) this.findViewById(R.id.cb_default);
    }

    @Override
    public void initData() {
        this.listProvince = DbHelper.getAreaByParentId(1);

    }

    @Override
    public void fillView() {
        setToolBar("添加地址");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_record_address, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tv_item_add_address:
                String inputPeople = this.edit_input_people.getText().toString();
                String inputPhonenum = this.edit_input_phone.getText().toString();
                if (TextUtils.isEmpty(inputPeople)) {
                    UIHelper.toastMessage(mContext, "添加收件人姓名");
                } else if (TextUtils.isEmpty(inputPhonenum) || inputPhonenum.length() != 11) {
                    UIHelper.toastMessage(mContext, "正确输入手机号码");
                } else {
                    addAddress();
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    private void addAddress() {
        String username = edit_input_people.getText().toString();
        String phone = edit_input_phone.getText().toString();
        String address = edit_input_address.getText().toString();
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("username", username);
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("address", address);
        params.addBodyParameter("sheng", this.province.getAreaName());
        params.addBodyParameter("shi", this.city.getAreaName());
        params.addBodyParameter("xian", this.county.getAreaName());
        params.addBodyParameter("isdefault", this.cb_default.isChecked() == true ? "1" : "0");
        http.send(HttpRequest.HttpMethod.POST, URLS.ADDRESS_ADD, params, new RequestCallBack<String>() {
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
                    UIHelper.toastMessage(mContext, "添加成功");
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rv_sheng:
                closeSoftKeyboard();
                showSheng();
                break;
            case R.id.rv_shi:
                closeSoftKeyboard();
                showShi();
                break;
            case R.id.rv_xian:
                closeSoftKeyboard();
                showXian();
                break;
        }
    }

    /**
     * 点击展示省份列表
     */
    private void showSheng() {
        if (listProvince == null) {
            return;
        }
        show_select_address("请选择省份", listProvince, AddAddressActivity.this,
                (dialog, arg1) -> {
                    province = listProvince.get(arg1);
                    listCity = DbHelper.getAreaByParentId(province.getAreaId());
                    String frequency = MessageFormat.format("<font color=\"#333333\">{0}</font>", province.getAreaName());
                    text_sheng.setText(Html.fromHtml(frequency));
                    text_shi.setText("请输入");
                    text_xian.setText("请输入");
                    edit_input_address.setText("");
                    dialog.cancel();
                });
    }

    /**
     * 点击展示市区列表
     */
    private void showShi() {
        text_xian.setText("请输入");
        if (listCity == null) {
            return;
        }
        show_select_address("请选择市区", listCity, AddAddressActivity.this,
                (dialog, arg1) -> {
                    city = listCity.get(arg1);
                    listCounty = DbHelper.getAreaByParentId(city.getAreaId());
                    String frequency = MessageFormat.format("<font color=\"#333333\">{0}</font>", city.getAreaName());
                    text_shi.setText(Html.fromHtml(frequency));
                    text_xian.setText("请输入");
                    edit_input_address.setText("");
                    dialog.cancel();
                });
    }

    /**
     * 点击展示地区列表
     */
    private void showXian() {
        if (listCounty == null) {
            return;
        }
        show_select_address("请选择地区", listCounty, AddAddressActivity.this,
                (dialog, arg1) -> {
                    county = listCounty.get(arg1);
                    String frequency = MessageFormat.format("<font color=\"#333333\">{0}</font>", county.getAreaName());
                    text_xian.setText(Html.fromHtml(frequency));
                    edit_input_address.setText("");
                    dialog.cancel();
                });
    }


    /**
     * 显示address列表
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void show_select_address(String title, List<AreaModel> list, Activity activity, DialogInterface.OnClickListener post) {
        final String[] items = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            AreaModel address = list.get(i);
            items[i] = address.getAreaName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setIcon(android.R.drawable.ic_lock_lock).setSingleChoiceItems(items, -1, post).create().show();
    }

    /**
     * 关闭输入软键方法
     */
    private void closeSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(AddAddressActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

