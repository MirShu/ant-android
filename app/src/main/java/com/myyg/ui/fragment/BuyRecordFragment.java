package com.myyg.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
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
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysEnums;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.JoinModel;
import com.myyg.model.MessageResult;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.rey.material.widget.Button;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/30.
 */
public class BuyRecordFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private int mParam1;

    private XRefreshView xRefreshView;

    private RecyclerView rv_bidding;

    private RecyclerAdapter<JoinModel> adapter;

    private List<JoinModel> listJoin = new ArrayList<>();

    private int pageIndex = 1;

    private String baseUrl = CommonHelper.getStaticBasePath();

    public BuyRecordFragment() {

    }

    /**
     * @param param1
     * @return
     */
    public static BuyRecordFragment newInstance(SysEnums.EnumJoinStatus param1) {
        BuyRecordFragment fragment = new BuyRecordFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1.getValue());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1, SysEnums.EnumJoinStatus.All.getValue());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bidding_record, container, false);
        this.xRefreshView = (XRefreshView) view.findViewById(R.id.xrefreshview);
        this.rv_bidding = (RecyclerView) view.findViewById(R.id.rv_bidding);
        this.bindRecycleView();
        this.bindListener();
        return view;
    }

    /**
     *
     */
    private void bindRecycleView() {
        this.listJoin.clear();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.xRefreshView.setAutoRefresh(true);
        this.adapter = new RecyclerAdapter<JoinModel>(this.getActivity(), this.listJoin, R.layout.item_bidding_record) {
            @Override
            public void convert(RecyclerViewHolder helper, JoinModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.iv_goods_img, imgUrl);
                helper.setText(R.id.tv_goods_name, item.getTitle());
                helper.setText(R.id.tv_qishu, MessageFormat.format("期号：{0}", item.getQishu()));
                helper.setText(R.id.tv_join_number, MessageFormat.format("我已参与：{0}人次", String.valueOf(item.getNumber())));
                TextView tv_details = helper.getView(R.id.tv_details);
                tv_details.setOnClickListener((v) -> {
                    toGoodsDetails(item.getId());
                });
                RelativeLayout rl_bottom_underway = helper.getView(R.id.rl_bottom_underway);
                RelativeLayout rl_bottom_announced = helper.getView(R.id.rl_bottom_announced);
                rl_bottom_underway.setVisibility(View.GONE);
                rl_bottom_announced.setVisibility(View.GONE);
                TextView tv_status = helper.getView(R.id.tv_status);
                if (item.getProcess() == SysEnums.EnumGoodsStatus.Announced.getValue() || mParam1 == SysEnums.EnumJoinStatus.Opened.getValue()) {
                    rl_bottom_announced.setVisibility(View.VISIBLE);
                    String winner = MessageFormat.format("获得者：<font color=\"#0171bb\">{0}</font>", item.getQ_user());
                    helper.setText(R.id.tv_winner, Html.fromHtml(winner));
                    String frequency = MessageFormat.format("<font color=\"#f95667\">{0}</font>人次", item.getQ_number());
                    helper.setText(R.id.tv_frequency, Html.fromHtml(frequency));
                    Button btn_buy_again = helper.getView(R.id.btn_buy_again);
                    btn_buy_again.setVisibility(View.GONE);
                    if (item.getAppend() == 1) {
                        btn_buy_again.setVisibility(View.VISIBLE);
                    }
                    btn_buy_again.setOnClickListener((view) -> toGoodsDetails(item.getId()));
                    tv_status.setText("已揭晓");
                    tv_status.setBackgroundColor(Color.parseColor("#90b2b2b2"));
                } else {
                    rl_bottom_underway.setVisibility(View.VISIBLE);
                    RoundCornerProgressBar rpb_progress = helper.getView(R.id.rpb_progress);
                    rpb_progress.setMax(100);
                    rpb_progress.setProgress(item.getCanyurenshu() * 100 / item.getZongrenshu());
                    helper.setText(R.id.tv_zongrenshu, MessageFormat.format("总需 {0}", String.valueOf(item.getZongrenshu())));
                    helper.setText(R.id.tv_shenyurenshu, MessageFormat.format("剩余 {0}", String.valueOf(item.getShenyurenshu())));
                    Button btn_append = helper.getView(R.id.btn_append);
                    btn_append.setVisibility(View.GONE);
                    if (item.getAppend() == 1) {
                        btn_append.setVisibility(View.VISIBLE);
                    }
                    btn_append.setOnClickListener((view) -> toGoodsDetails(item.getId()));
                    tv_status.setText("进行中");
                    tv_status.setBackgroundColor(Color.parseColor("#9039b44a"));
                }
            }
        };
        this.rv_bidding.setHasFixedSize(true);
        this.rv_bidding.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.rv_bidding.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(this.getActivity()));
    }

    private void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    listJoin.clear();
                    loadData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }
        });
        this.adapter.setOnItemClickListener((parent, position) ->
        {
            JoinModel model = listJoin.get(position);
            this.toGoodsDetails(model.getId());
        });
    }

    /**
     * 打开商品详情页面
     *
     * @param goodsId
     */
    private void toGoodsDetails(int goodsId) {
        Bundle bundle = new Bundle();
        bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, goodsId);
        UIHelper.startActivity(this.getActivity(), GoodsDetailsActivity.class, bundle);
    }

    /**
     *
     */
    private void loadData() {
        String url = URLS.USER_JOIN_LIST;
        if (this.mParam1 == SysEnums.EnumJoinStatus.Opened.getValue()) {
            url = URLS.USER_OPENED_LIST;
        }
        if (this.mParam1 == SysEnums.EnumJoinStatus.Process.getValue()) {
            url = URLS.USER_PROCESS_LIST;
        }
        url = MessageFormat.format("{0}/{1}/{2}", url, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<JoinModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    list = JSON.parseArray(message.getData(), JoinModel.class);
                    listJoin.addAll(list);
                    adapter.notifyDataSetChanged();
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
}
