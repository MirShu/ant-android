package com.myyg.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
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
import com.myyg.model.LastLotteryModel;
import com.myyg.model.MessageResult;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.RoundedImageView;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/29.
 */
public class PastGoodsActivity extends BaseActivity {
    public static final String GOODS_SID_TAG = "goods_sid_tag";

    public static final int GOODS_GO_SHOP_REQUEST_CODE = 0x01;

    private LastLotteryModel lastLotteryModel;

    private int goodsId;

    private String baseUrl = CommonHelper.getStaticBasePath();

    private TextView tv_latest_qishu;

    private int pageIndex = 1;

    private XRefreshView xRefreshView;

    private RecyclerView rv_past_goods;

    private RecyclerAdapter<LastLotteryModel.PrevModel> adapter;

    private List<LastLotteryModel.PrevModel> listPastGoods = new ArrayList<>();

    private LinearLayout ll_latest;

    private LastLotteryModel.LatestModel latestModel;

    private ShopCartBroadcastReceiver mBroadcastReceiver;

    @Override
    public void initView() {
        setContentView(R.layout.activity_past_goods);
        this.rv_past_goods = (RecyclerView) findViewById(R.id.rv_past_goods);
        this.xRefreshView = (XRefreshView) findViewById(R.id.xRefreshView);
        this.tv_latest_qishu = (TextView) findViewById(R.id.tv_latest_qishu);
        this.ll_latest = (LinearLayout) this.findViewById(R.id.ll_latest);
    }

    @Override
    public void initData() {
        this.goodsId = this.getIntent().getIntExtra(GOODS_SID_TAG, 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SysConstant.ACTION_MYYG_RECEIVE_SHOP_CART);
        if (this.mBroadcastReceiver == null) {
            this.mBroadcastReceiver = new ShopCartBroadcastReceiver();
        }
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void fillView() {
        this.setToolBar("往期揭晓");
        this.bindRecycleView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_latest:
                if (this.latestModel == null) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, this.latestModel.getNext_id());
                UIHelper.startActivity(this.mContext, GoodsDetailsActivity.class, bundle);
                break;
        }
    }

    /**
     * 绑定往期揭晓数据
     */
    private void bindRecycleView() {
        this.listPastGoods.clear();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(PastGoodsActivity.this);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.adapter = new RecyclerAdapter<LastLotteryModel.PrevModel>(PastGoodsActivity.this, this.listPastGoods, R.layout.item_to_announce) {
            @Override
            public void convert(RecyclerViewHolder helper, LastLotteryModel.PrevModel item, int position) {
                RoundedImageView riv_user_photo = helper.getView(R.id.riv_user_photo);
                TextView tv_q_user = helper.getView(R.id.tv_q_user);
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getImg());
                helper.setImageUrl(R.id.riv_user_photo, imgUrl);
                helper.setText(R.id.tv_prev_qishu, MessageFormat.format("期号：{0}", item.getQishu()));
                helper.setText(R.id.tv_q_end_time, MessageFormat.format("最新揭晓：{0}", DateHelper.getDefaultDate(item.getQ_end_time() * 1000)));
                helper.setText(R.id.tv_ip, MessageFormat.format("IP：{0}", item.getIp()));
                helper.setText(R.id.tv_q_uid, MessageFormat.format("用户ID：{0}(唯一不变标识)", item.getQ_show_uid()));
                String qUser = MessageFormat.format("获得者：<font color=\"#1a7cc7\">{0}</font>", item.getQ_user());
                helper.setText(R.id.tv_q_user, Html.fromHtml(qUser));
                String qUserCode = MessageFormat.format("幸运号：<font color=\"#c62334\">{0}</font>", item.getQ_user_code());
                helper.setText(R.id.tv_q_user_code, Html.fromHtml(qUserCode));
                String gonNumber = MessageFormat.format("本期参与：<font color=\"#c62334\">{0}</font>人次", item.getGonumber());
                helper.setText(R.id.tv_gonumber, Html.fromHtml(gonNumber));
                riv_user_photo.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(HisPersonalCenterActivity.HIS_USER_ID, item.getQ_uid());
                    UIHelper.startActivityForResult(mContext, HisPersonalCenterActivity.class, GOODS_GO_SHOP_REQUEST_CODE, bundle);
                });
                tv_q_user.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(HisPersonalCenterActivity.HIS_USER_ID, item.getQ_uid());
                    UIHelper.startActivityForResult(mContext, HisPersonalCenterActivity.class, GOODS_GO_SHOP_REQUEST_CODE, bundle);
                });
            }
        };
        this.rv_past_goods.setHasFixedSize(true);
        this.rv_past_goods.setLayoutManager(new LinearLayoutManager(PastGoodsActivity.this));
        this.rv_past_goods.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(this.mContext));
        //this.xRefreshView.setAutoRefresh(true);
    }

    /**
     * 绑定监听事件
     */
    public void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    listPastGoods.clear();
                    loadData();
                }, 500);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }
        });
        this.adapter.setOnItemClickListener((parent, position) -> {
            LastLotteryModel.PrevModel goods = this.listPastGoods.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, goods.getId());
            UIHelper.startActivityForResult(this.mContext, GoodsDetailsActivity.class, GOODS_GO_SHOP_REQUEST_CODE, bundle);
        });
    }

    /**
     * 往期揭晓获取数据源
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}/{3}", URLS.GOODS_LAST_LOTTERY, this.goodsId, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        UIHelper.toastMessage(mContext, message.getMsg());
                        return;
                    }
                    lastLotteryModel = JSON.parseObject(message.getData(), LastLotteryModel.class);
                    ll_latest.setVisibility(View.GONE);
                    latestModel = lastLotteryModel.getLatest();
                    if (latestModel.getShowtime().equals("Y")) {
                        ll_latest.setVisibility(View.VISIBLE);
                        tv_latest_qishu.setText(MessageFormat.format("期号：{0}   即将揭晓，正在倒计时...", latestModel.getQishu()));
                    }
                    List<LastLotteryModel.PrevModel> prevModelArrayList = lastLotteryModel.getPrev();
                    if (prevModelArrayList == null || prevModelArrayList.size() == 0) {
                        xRefreshView.stopRefresh();
                        return;
                    }
                    listPastGoods.addAll(prevModelArrayList);
                    adapter.notifyDataSetChanged();
                    if (prevModelArrayList.size() < SysConstant.PAGE_SIZE) {
                        xRefreshView.setPullLoadEnable(false);
                    }
                    if (pageIndex == 1) {
                        xRefreshView.stopRefresh();
                    } else {
                        xRefreshView.stopLoadMore();
                    }
                    if (prevModelArrayList.size() == SysConstant.PAGE_SIZE) {
                        pageIndex++;
                    }
                } catch (Exception ex) {
                    xRefreshView.stopRefresh();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case GOODS_GO_SHOP_REQUEST_CODE:
                this.goShopCart();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBroadcastReceiver != null) {
            this.unregisterReceiver(this.mBroadcastReceiver);
        }
    }

    /**
     * 去购物车
     */
    private void goShopCart() {
        setResult(RESULT_OK);
        this.finish();
    }

    /**
     * 进入购物车广播接收
     */
    public class ShopCartBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case SysConstant.ACTION_MYYG_RECEIVE_SHOP_CART:
                    goShopCart();
                    break;
            }
        }
    }
}
