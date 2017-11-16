package com.myyg.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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
import com.myyg.model.AddressModel;
import com.myyg.model.AreaModel;
import com.myyg.model.MessageResult;
import com.myyg.utils.UIHelper;

import java.text.MessageFormat;
import java.util.List;

public class EditAddressActivity extends BaseActivity {

    public static final String ADDRESS_CODE = "address_code";

    private EditText edit_input_people, edit_input_phone, edit_input_address;

    private CheckBox cb_default;

    private TextView text_sheng, text_shi, text_xian;

    private AddressModel model;

    private List<AreaModel> listProvince;

    private List<AreaModel> listCity;

    private List<AreaModel> listCounty;

    private AreaModel province;

    private AreaModel city;

    private AreaModel county;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_edit_address);
        this.edit_input_people = (EditText) findViewById(R.id.edit_input_people);
        this.edit_input_phone = (EditText) findViewById(R.id.edit_input_phone);
        this.edit_input_address = (EditText) findViewById(R.id.edit_input_address);
        this.text_sheng = (TextView) findViewById(R.id.text_sheng);
        this.text_shi = (TextView) findViewById(R.id.text_shi);
        this.text_xian = (TextView) findViewById(R.id.text_xian);
        this.cb_default = (CheckBox) this.findViewById(R.id.cb_default);
    }

    /**
     * 绑定要修改的地址相关参数信息
     */
    @Override
    public void initData() {
        this.model = (AddressModel) this.getIntent().getSerializableExtra(ADDRESS_CODE);
        this.listProvince = DbHelper.getAreaByParentId(1);
        province = DbHelper.getAreaByName(this.model.getSheng());
        if (province != null) {
            this.listCity = DbHelper.getAreaByParentId(province.getAreaId());
        }
        city = DbHelper.getAreaByName(this.model.getShi());
        if (city != null) {
            this.listCounty = DbHelper.getAreaByParentId(city.getAreaId());
        }
        county = DbHelper.getAreaByName(this.model.getXian());
        this.edit_input_people.setText(this.model.getShouhuoren());
        this.edit_input_phone.setText(this.model.getMobile());
        String sheng = MessageFormat.format("<font color=#333333>{0}</font>", model.getSheng());
        this.text_sheng.setText(Html.fromHtml(sheng));
        String shi = MessageFormat.format("<font color=#333333>{0}</font>", model.getShi());
        this.text_shi.setText(Html.fromHtml(shi));
        String xian = MessageFormat.format("<font color=#333333>{0}</font>", model.getXian());
        this.text_xian.setText(Html.fromHtml(xian));
        this.edit_input_address.setText(this.model.getJiedao());
        this.cb_default.setChecked(this.model.getIsdefault());
    }

    @Override
    public void fillView() {
        setToolBar("添加地址");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_edit_address, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tv_item_edit_address:
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
     * 提交编辑后的地址参数
     */
    private void addAddress() {
        String username = edit_input_people.getText().toString();
        String phone = edit_input_phone.getText().toString();
        String address = edit_input_address.getText().toString();
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("id", model.getId());
        params.addBodyParameter("username", username);
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("address", address);
        params.addBodyParameter("sheng", this.province.getAreaName());
        params.addBodyParameter("shi", this.city.getAreaName());
        params.addBodyParameter("xian", this.county.getAreaName());
        params.addBodyParameter("isdefault", this.cb_default.isChecked() == true ? "1" : "0");
        http.send(HttpRequest.HttpMethod.POST, URLS.ADDRESS_UPDATE, params, new RequestCallBack<String>() {
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
     * 省市县地址展示公用列表
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void show_select_address(String title, List<AreaModel> list,
                                    Activity activity, DialogInterface.OnClickListener post, String selectAreaName) {
        final String[] items = new String[list.size()];
        int itemIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            AreaModel address = list.get(i);
            items[i] = address.getAreaName();
            if (address.getAreaName().equals(selectAreaName)) {
                itemIndex = i;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setIcon(android.R.drawable.ic_lock_lock)
                .setSingleChoiceItems(items, itemIndex, post).create().show();
    }

    /**
     * 关闭输入软键方法
     */
    private void closeSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(EditAddressActivity.this.getCurrentFocus().getWindowToken()
                , InputMethodManager.HIDE_NOT_ALWAYS);

    }

    /**
     * 点击展示省级列表
     */
    private void showSheng() {
        if (listProvince == null) {
            return;
        }
        show_select_address("请选择省份", listProvince, EditAddressActivity.this,
                (dialog, arg1) -> {
                    province = listProvince.get(arg1);
                    listCity = DbHelper.getAreaByParentId(province.getAreaId());
                    String frequency = MessageFormat.format("<font color=\"#333333\">{0}</font>", province.getAreaName());
                    this.text_sheng.setText(Html.fromHtml(frequency));
                    this.text_shi.setText("请输入");
                    this.text_xian.setText("请输入");
                    this.edit_input_address.setText("");
                    dialog.cancel();
                }, province.getAreaName());
    }

    /**
     * 点击展示市级列表
     */
    private void showShi() {
        if (listCity == null) {
            return;
        }
        show_select_address("请选择市区", listCity, EditAddressActivity.this,
                (dialog, arg1) -> {
                    city = listCity.get(arg1);
                    listCounty = DbHelper.getAreaByParentId(city.getAreaId());
                    String frequency = MessageFormat.format("<font color=\"#333333\">{0}</font>", city.getAreaName());
                    text_shi.setText(Html.fromHtml(frequency));
                    text_xian.setText("请输入");
                    this.edit_input_address.setText("");
                    dialog.cancel();
                }, city.getAreaName());
    }

    /**
     * 点击展示地区列表
     */
    private void showXian() {
        if (listCounty == null) {
            return;
        }
        show_select_address("请选择地区", listCounty, EditAddressActivity.this,
                (dialog, arg1) -> {
                    county = listCounty.get(arg1);
                    String frequency = MessageFormat.format("<font color=\"#333333\">{0}</font>", county.getAreaName());
                    text_xian.setText(Html.fromHtml(frequency));
                    this.edit_input_address.setText("");
                    dialog.cancel();
                }, county.getAreaName());
    }
}
