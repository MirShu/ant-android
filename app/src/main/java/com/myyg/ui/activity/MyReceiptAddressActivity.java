package com.myyg.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.andview.refreshview.XRefreshView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.adapter.recycler.RecyclerAdapter;
import com.myyg.adapter.recycler.RecyclerViewHolder;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.listener.OnDialogListener;
import com.myyg.model.AddressModel;
import com.myyg.model.MessageResult;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created  on 2016/5/27.
 */
public class MyReceiptAddressActivity extends BaseActivity {

    private final static int REQUEST_ADD_CODE = 0x01;

    private final static int REQUEST_EDIT_CODE = 0x02;

    private RecyclerView rv_address;

    private XRefreshView xRefreshView;

    private RecyclerAdapter<AddressModel> adapter;

    private List<AddressModel> listAddress = new ArrayList<>();

    private RelativeLayout rl_empty_address;

    @Override
    public void initView() {
        setContentView(R.layout.activity_my_receipt_address);
        this.xRefreshView = (XRefreshView) this.findViewById(R.id.xRefreshView);
        this.rv_address = (RecyclerView) this.findViewById(R.id.rv_address);
        this.rl_empty_address = (RelativeLayout) this.findViewById(R.id.rl_empty_address);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("我的收货地址");
        this.bindLikeRecycleView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                UIHelper.startActivityForResult(this.mContext, AddAddressActivity.class, REQUEST_ADD_CODE, null);
                break;
        }
    }

    private void bindLikeRecycleView() {
        this.loadData();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(MyReceiptAddressActivity.this);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.adapter = new RecyclerAdapter<AddressModel>(MyReceiptAddressActivity.this, this.listAddress, R.layout.item_address) {
            @Override
            public void convert(RecyclerViewHolder helper, AddressModel item, int position) {
                helper.setText(R.id.tv_shouhuoren, item.getShouhuoren());
                helper.setText(R.id.tv_sheng, item.getSheng());
                helper.setText(R.id.tv_shi, item.getShi());
                helper.setText(R.id.tv_xian, item.getXian());
                helper.setText(R.id.tv_jiedao, item.getJiedao());
                helper.setText(R.id.tv_mobile, item.getMobile());

                TextView tv_default = helper.getView(R.id.tv_default);
                tv_default.setVisibility(View.GONE);
                if (item.getIsdefault()) {
                    tv_default.setVisibility(View.VISIBLE);
                }
                TextView tv_delete_address = helper.getView(R.id.tv_delete_address);
                tv_delete_address.setOnClickListener((view) -> {
                    AddressModel model = listAddress.get(position);
                    deleteAddressShow(model);
                });
                TextView tv_edit_address = helper.getView(R.id.tv_edit_address);
                tv_edit_address.setOnClickListener((view) -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(EditAddressActivity.ADDRESS_CODE, item);
                    UIHelper.startActivityForResult(mContext, EditAddressActivity.class, REQUEST_EDIT_CODE, bundle);
                });
            }
        };
        this.rv_address.setHasFixedSize(true);
        this.rv_address.setLayoutManager(new GridLayoutManager(MyReceiptAddressActivity.this, 1, LinearLayoutManager.VERTICAL, false));
        this.rv_address.setAdapter(this.adapter);
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    loadData();
                }, 1000);
            }
        });
    }

    /**
     * 删除地址方法
     */
    private void deleteAddressShow(AddressModel model) {
        UIHelper.showDialog(this.mContext, "你确定要删除当前选中地址吗?", "取消", "确定", new OnDialogListener() {
            @Override
            public void ok(com.rey.material.app.Dialog dialog) {
                deleteAddress(model);
            }

            @Override
            public void cancel(com.rey.material.app.Dialog dialog) {

            }
        });
    }

    private void deleteAddress(AddressModel model) {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("id", model.getId());
        http.send(HttpRequest.HttpMethod.POST, URLS.ADDRESS_DELETE, params, new RequestCallBack<String>() {
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
                    loadData();
                    UIHelper.toastMessage(mContext, "删除成功");
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
     * 获取地址列表数据
     */
    private void loadData() {
        this.listAddress.clear();
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.ADDRESS_ALL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                xRefreshView.stopRefresh();
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, message.getMsg());
                    return;
                }
                List<AddressModel> list = JSON.parseArray(message.getData(), AddressModel.class);
                listAddress.addAll(list);
                if (listAddress.size() > 0) {
                    xRefreshView.setVisibility(View.VISIBLE);
                    rl_empty_address.setVisibility(View.GONE);
                } else {
                    xRefreshView.setVisibility(View.GONE);
                    rl_empty_address.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopRefresh();
            }
        });
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
                UIHelper.startActivityForResult(this.mContext, AddAddressActivity.class, REQUEST_ADD_CODE, null);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_ADD_CODE:
                this.loadData();
                break;
            case REQUEST_EDIT_CODE:
                this.loadData();
                break;
        }
    }
}
