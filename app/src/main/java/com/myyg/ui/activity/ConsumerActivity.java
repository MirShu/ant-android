package com.myyg.ui.activity;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import com.myyg.model.ConsumerModel;
import com.myyg.model.GoodsListModel;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsumerActivity extends BaseActivity {
    private RecyclerView rv_consumer;

    private XRefreshView xRefreshView;

    private RecyclerAdapter<ConsumerModel> adapter;

    private List<ConsumerModel> listConsumer = new ArrayList<>();

    private int pageIndex = 1;

    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_consumer);
        this.xRefreshView = (XRefreshView) this.findViewById(R.id.xRefreshView);
        this.rv_consumer = (RecyclerView) this.findViewById(R.id.rv_consumer);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.adapter = new RecyclerAdapter<ConsumerModel>(this.mContext, this.listConsumer, R.layout.item_consumer) {
            @Override
            public void convert(RecyclerViewHolder helper, ConsumerModel item, int position) {
                helper.setText(R.id.tv_money, MessageFormat.format("￥{0}", df.format(item.getMoney())));
                helper.setText(R.id.tv_time, MessageFormat.format("{0}", DateHelper.getYYYYMMDD(new Date(item.getTime() * 1000))));
            }
        };
        MyygRefreshHeader header = new MyygRefreshHeader(this.mContext);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setPullLoadEnable(true);
        this.xRefreshView.setMoveForHorizontal(false);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(this.mContext));
        this.rv_consumer.setHasFixedSize(true);
        this.rv_consumer.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rv_consumer.setAdapter(this.adapter);
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
        this.loadData();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        String url = MessageFormat.format("{0}?pageIndex={1}&pageSize={2}", URLS.USER_BUY_LOG, this.pageIndex, SysConstant.PAGE_SIZE);
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
                    listConsumer.clear();
                    xRefreshView.stopRefresh();
                }
                List<ConsumerModel> list = JSON.parseArray(message.getData(), ConsumerModel.class);
                listConsumer.addAll(list);
                adapter.notifyDataSetChanged();
                if (list.size() < SysConstant.PAGE_SIZE) {
                    xRefreshView.setLoadComplete(true);
                } else {
                    xRefreshView.setLoadComplete(false);
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
}
