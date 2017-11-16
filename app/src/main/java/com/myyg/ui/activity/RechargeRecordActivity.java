package com.myyg.ui.activity;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
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
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.RechargeRecordModel;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/26.
 */
public class RechargeRecordActivity extends BaseActivity {
    private int pageIndex = 1;

    private RecyclerAdapter<RechargeRecordModel> adapter;

    private List<RechargeRecordModel> listRecharge = new ArrayList<>();

    private XRefreshView xRefreshView;

    private RecyclerView rv_recharge_record;

    private RelativeLayout rl_other;


    @Override
    public void initView() {
        setContentView(R.layout.activity_recharge_record);
        this.rv_recharge_record = (RecyclerView) findViewById(R.id.rv_recharge_record);
        this.xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        this.rl_other = (RelativeLayout) this.findViewById(R.id.rl_other);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("充值记录");
        this.bindRecycleView();
        this.bindListener();
    }

    /**
     * 绑定用户充值记录数据
     */
    private void bindRecycleView() {
        this.listRecharge.clear();
        this.loadData();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(RechargeRecordActivity.this);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.adapter = new RecyclerAdapter<RechargeRecordModel>(RechargeRecordActivity.this, this.listRecharge, R.layout.item_recharge_log) {
            @Override
            public void convert(RecyclerViewHolder helper, RechargeRecordModel item, int position) {
                helper.setText(R.id.tv_pay_type, item.getPay_type());
                helper.setText(R.id.tv_time, DateHelper.getDefaultDate(new Date(Long.parseLong(item.getTime()) * 1000)));
                helper.setText(R.id.tv_status, item.getStatus());
                helper.setText(R.id.tv_money, item.getMoney() + "元");
            }
        };
        this.rv_recharge_record.setHasFixedSize(true);
        this.rv_recharge_record.setLayoutManager(new LinearLayoutManager(RechargeRecordActivity.this));
        this.rv_recharge_record.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(RechargeRecordActivity.this));
    }

    /**
     *
     */
    public void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            public void onRefresh() {
                new Handler().postDelayed(() -> xRefreshView.stopRefresh(), 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }

        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_recharge:
                UIHelper.startActivity(this.mContext, AccountRechargeActivity.class);
                break;
        }
    }

    /**
     * 获取用户充值记录数据
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.USER_RECHARGE_LOG, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<RechargeRecordModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    list = JSON.parseArray(message.getData(), RechargeRecordModel.class);
                    listRecharge.addAll(list);
                    adapter.notifyDataSetChanged();
                }
                if (listRecharge.size() == 0) {
                    rl_other.setVisibility(View.VISIBLE);
                }
                if (list.size() < SysConstant.PAGE_SIZE) {
                    xRefreshView.setPullLoadEnable(false);
                }
                if (pageIndex == 1) {
                    xRefreshView.stopRefresh();
                } else {
                    xRefreshView.stopLoadMore();
                }
                if (list.size() == SysConstant.PAGE_SIZE) {
                    pageIndex++;
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_recharge, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tv_item_recharge:
                UIHelper.startActivity(this.mContext, AccountRechargeActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
