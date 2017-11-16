package com.myyg.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
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
import com.myyg.model.MessageResult;
import com.myyg.model.PresenRecordModel;
import com.myyg.model.UserModel;
import com.myyg.utils.DateHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshFooter;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/30.
 */
public class MemberAwardActivity extends BaseActivity {
    private XRefreshView xRefreshView;

    private TextView tv_reward;

    private TextView tv_percent;

    private int pageIndex = 1;

    private RecyclerAdapter<PresenRecordModel> adapter;

    private List<PresenRecordModel> listPresent = new ArrayList<>();

    private RecyclerView rv_present_record;

    private String reward;

    private String percent;

    private String reward_total;

    private UserModel user = BaseApplication.getUser();

    private TextView tv_empty_tips;

    @Override
    public void initView() {
        setContentView(R.layout.activity_member_award);
        this.xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        this.rv_present_record = (RecyclerView) findViewById(R.id.rv_present_record);
        this.tv_reward = (TextView) findViewById(R.id.tv_reward);
        this.tv_percent = (TextView) findViewById(R.id.tv_percent);
        this.tv_empty_tips = (TextView) this.findViewById(R.id.tv_empty_tips);
        this.bindRecycleView();
        this.bindListener();
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("会员返利");
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.atm_button:
                UIHelper.startActivity(this.mContext, ATMsActivity.class);
                break;
            case R.id.view_friends_button:
                bundle.putString(FriendsActivity.REWARD_TAG, this.reward);
                bundle.putString(FriendsActivity.PERCENT_TAG, this.percent);
                UIHelper.startActivity(this.mContext, FriendsActivity.class, bundle);
                break;
            case R.id.qr_code_button:
                UIHelper.startActivity(this.mContext, QRcodeActivity.class);
                break;
            case R.id.btn_copy_link:
                ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(this.user.getLink());
                UIHelper.toastMessage(mContext, "复制成功");
                break;
            case R.id.rl_commission:
                bundle.putString(CommissionActivity.COMMISSION_REWARD_TAG, this.reward);
                bundle.putString(CommissionActivity.COMMISSION_REWARD_TOTAL_TAG, this.reward_total);
                UIHelper.startActivity(this.mContext, CommissionActivity.class, bundle);
                break;
        }
    }

    /**
     * 绑定用户提现记录数据
     */
    private void bindRecycleView() {
        this.listPresent.clear();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(MemberAwardActivity.this);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.adapter = new RecyclerAdapter<PresenRecordModel>(MemberAwardActivity.this, this.listPresent, R.layout.item_present_record) {
            @Override
            public void convert(RecyclerViewHolder helper, PresenRecordModel item, int position) {
                helper.setText(R.id.tv_present_time, MessageFormat.format("{0}", DateHelper.getYYYYMMDD(new Date(item.getTime() * 1000))));
                helper.setText(R.id.tv_tv_present_money, MessageFormat.format("{0}元", item.getMoney()));
                View split_horizontal = helper.getView(R.id.split_horizontal);
                split_horizontal.setVisibility(View.VISIBLE);
                if (position == listPresent.size() - 1) {
                    split_horizontal.setVisibility(View.GONE);
                }
            }
        };
        this.rv_present_record.setHasFixedSize(true);
        this.rv_present_record.setLayoutManager(new LinearLayoutManager(MemberAwardActivity.this));
        this.rv_present_record.setAdapter(this.adapter);
        //this.adapter.setCustomLoadMoreView(new MyygRefreshFooter(this.mContext));
        this.loadData();
    }

    public void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    xRefreshView.setPullLoadEnable(true);
                    pageIndex = 1;
                    listPresent.clear();
                    bindData();
                    loadData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }

        });
    }

    /**
     * 获取用户提现记录数据
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.USER_CASH_LOG, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                List<PresenRecordModel> list = new ArrayList<>();
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() == SysStatusCode.SUCCESS) {
                        list = JSON.parseArray(message.getData(), PresenRecordModel.class);
                        listPresent.addAll(list);
                        tv_empty_tips.setVisibility(View.GONE);
                        if (listPresent.size() == 0) {
                            tv_empty_tips.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    if (list.size() < SysConstant.PAGE_SIZE) {
                        xRefreshView.setPullLoadEnable(false);
                    }
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {
                    if (pageIndex == 1) {
                        xRefreshView.stopRefresh();
                    } else {
                        xRefreshView.stopLoadMore();
                    }
                    if (list.size() == SysConstant.PAGE_SIZE) {
                        pageIndex++;
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.bindData();
    }

    /**
     *
     */
    private void bindData() {
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
                    reward = String.valueOf(json.get("reward"));
                    percent = String.valueOf(json.get("percent"));
                    reward_total = String.valueOf(json.get("reward_total"));
                    tv_reward.setText(reward);
                    tv_percent.setText(percent);
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {
                    xRefreshView.stopRefresh();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopRefresh();
            }
        });
    }
}
