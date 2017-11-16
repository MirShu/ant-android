package com.myyg.ui.activity;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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
import com.myyg.model.CommissionModel;
import com.myyg.model.MessageResult;
import com.myyg.ui.view.RechargeDialog;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.extend.ScrollViewEx;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommissionActivity extends BaseActivity {
    public static final String COMMISSION_REWARD_TAG = "commission_reward_tag";
    public static final String COMMISSION_REWARD_TOTAL_TAG = "commission_reward_total_tag";
    private int pageIndex = 1;

    private XRefreshView xRefreshView;

    private List<CommissionModel> listCommission = new ArrayList<>();

    private RecyclerAdapter<CommissionModel> adapter;

    private TextView tv_empty_tips;

    private String reward, reward_total;

    private TextView tv_reward, tv_reward_total;

    private RecyclerView rv_commission;

    private DecimalFormat df = new DecimalFormat("0.00");

    private ScrollViewEx sv_wrap;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_commission);
        this.sv_wrap = (ScrollViewEx) this.findViewById(R.id.sv_wrap);
        this.xRefreshView = (XRefreshView) this.findViewById(R.id.xRefreshView);
        this.tv_empty_tips = (TextView) this.findViewById(R.id.tv_empty_tips);
        this.tv_reward = (TextView) this.findViewById(R.id.tv_reward);
        this.tv_reward_total = (TextView) this.findViewById(R.id.tv_reward_total);
        this.rv_commission = (RecyclerView) this.findViewById(R.id.rv_commission);
    }

    @Override
    public void initData() {
        this.reward = this.getIntent().getStringExtra(COMMISSION_REWARD_TAG);
        this.reward_total = this.getIntent().getStringExtra(COMMISSION_REWARD_TOTAL_TAG);
        this.tv_reward.setText(MessageFormat.format("{0}元", reward));
        this.tv_reward_total.setText(MessageFormat.format("{0}元", reward_total));
    }

    @Override
    public void fillView() {
        this.setToolBar("佣金明细");
        this.adapter = new RecyclerAdapter<CommissionModel>(this.mContext, this.listCommission, R.layout.item_commission) {
            @Override
            public void convert(RecyclerViewHolder helper, CommissionModel item, int position) {
                helper.setText(R.id.tv_time, MessageFormat.format("{0}", DateHelper.getYYYYMMDD(new Date(item.getTime() * 1000))));
                helper.setText(R.id.tv_money, item.getMoney());
                helper.setText(R.id.tv_name, item.getName());
                helper.setText(R.id.tv_ygmoney, item.getYgmoney());
            }
        };
        MyygRefreshHeader header = new MyygRefreshHeader(this.mContext);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setPullLoadEnable(true);
        this.xRefreshView.setMoveForHorizontal(false);
        //this.xRefreshView.setCustomFooterView(new XRefreshViewFooter(this.mContext));
        //this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(this.mContext));
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    loadData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }
        });
        this.xRefreshView.setAutoLoadMore(true);
        this.rv_commission.setHasFixedSize(true);
        this.rv_commission.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rv_commission.setAdapter(this.adapter);
        this.loadData();
    }

    @Override
    public void bindListener() {
        super.bindListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cash:
                UIHelper.startActivity(this.mContext, ATMsActivity.class);
                break;
            case R.id.btn_recharge:
                RechargeDialog dialog = new RechargeDialog(this.mContext, Float.parseFloat(this.reward));
                dialog.show();
                dialog.setListener(money -> {
                    this.rewardToAccount(money);
                });
                break;
        }
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.USER_COMMISSION_DETAILS, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                xRefreshView.stopRefresh();
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, message.getMsg());
                    return;
                }
                if (pageIndex == 1) {
                    listCommission.clear();
                    xRefreshView.stopRefresh();
                }
                List<CommissionModel> list = JSON.parseArray(message.getData(), CommissionModel.class);
                if (list == null) {
                    list = new ArrayList<>();
                }
                listCommission.addAll(list);
                tv_empty_tips.setVisibility(View.GONE);
                if (listCommission.size() == 0) {
                    tv_empty_tips.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                if (list.size() < SysConstant.PAGE_SIZE) {
                    xRefreshView.setLoadComplete(true);
                } else {
                    xRefreshView.setLoadComplete(false);
                }
                if (list.size() >= SysConstant.PAGE_SIZE) {
                    pageIndex++;
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopRefresh();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.sv_wrap.smoothScrollTo(0, 0);
    }

    /**
     * @param money
     */
    private void rewardToAccount(float money) {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("reward", String.valueOf(money));
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_REWARD_TO_ACCOUNT, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                UIHelper.showLoading(mContext);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                UIHelper.hideLoading();
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, result.getMsg());
                    return;
                }
                float currentReward = Float.parseFloat(reward) - money;
                reward = String.valueOf(currentReward);
                tv_reward.setText(df.format(currentReward));
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
            }
        });
    }
}
