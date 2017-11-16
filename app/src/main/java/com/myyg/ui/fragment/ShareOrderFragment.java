package com.myyg.ui.fragment;

import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.model.ShareModel;
import com.myyg.model.UserShareModel;
import com.myyg.ui.activity.AddShareActivity;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.CustomGifHeader;
import com.myyg.widget.xrefreshview.MyygRefreshFooter;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.rey.material.widget.Button;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShareOrderFragment extends Fragment {
    private static final String SHARED_TAG = "shared_tag";
    private int shardType;

    private XRefreshView xRefreshView;

    private RecyclerView rv_share;

    private List<UserShareModel.ShareOrderModel> listShare = new ArrayList<>();

    private RecyclerAdapter<UserShareModel.ShareOrderModel> adapter;

    private List<UserShareModel.ShareOrderModel> listGoods = new ArrayList<>();

    private RecyclerAdapter<UserShareModel.ShareOrderModel> goodsAdapter;

    private int pageIndex = 1;

    private String baseUrl = CommonHelper.getStaticBasePath();

    public ShareOrderFragment() {

    }

    /**
     * @param shared
     * @return
     */
    public static ShareOrderFragment newInstance(SysEnums.EnumBaskOrder shared) {
        ShareOrderFragment fragment = new ShareOrderFragment();
        Bundle args = new Bundle();
        args.putInt(SHARED_TAG, shared.getValue());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shardType = getArguments().getInt(SHARED_TAG, SysEnums.EnumBaskOrder.Yes.getValue());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_order, container, false);
        this.xRefreshView = (XRefreshView) view.findViewById(R.id.xrefreshview);
        this.rv_share = (RecyclerView) view.findViewById(R.id.rv_share);
        if (shardType == SysEnums.EnumBaskOrder.Yes.getValue()) {
            this.loadShare();
        } else {
            this.loadGoods();
        }
        return view;
    }

    /**
     * 加载已晒单信息
     */
    private void loadShare() {
        this.listShare.clear();
        this.bindShareRecycleView();
        this.bindShareListener();
        this.loadShareData();
    }

    /**
     * 绑定已晒单视图
     */
    private void bindShareRecycleView() {
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.adapter = new RecyclerAdapter<UserShareModel.ShareOrderModel>(this.getActivity(), listShare, R.layout.item_share) {
            @Override
            public void convert(RecyclerViewHolder helper, UserShareModel.ShareOrderModel item, int position) {
                helper.setText(R.id.tv_title, item.getTitle());
                helper.setText(R.id.tv_sd_title, item.getSd_title());
                helper.setText(R.id.tv_sd_content, Html.fromHtml(item.getSd_content()));
                helper.setText(R.id.tv_sd_time, MessageFormat.format("晒单时间：{0}", DateHelper.getYYYYMMMDDHHMM(new Date(item.getSd_time() * 1000))));
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getSd_thumbs());
                helper.setImageUrl(R.id.iv_goods_img, imgUrl);
                String status = item.getSd_status() == SysEnums.EnumYesNo.Yes.getValue() ? "审核通过" : "正在审核";
                String htmlStatus = MessageFormat.format("状态：<font color=\"{0}\">{1}</font>", item.getSd_status() == SysEnums.EnumYesNo.Yes.getValue() ? "#39b44a" : "#c62435", status);
                helper.setText(R.id.tv_status, Html.fromHtml(htmlStatus));
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
            }
        };
        this.rv_share.setHasFixedSize(true);
        this.rv_share.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.rv_share.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new MyygRefreshFooter(this.getActivity()));
    }

    /**
     * 绑定已晒单事件
     */
    private void bindShareListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    listShare.clear();
                    loadShareData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadShareData();
            }
        });
        this.adapter.setOnItemClickListener((view, position) -> {
            UserShareModel.ShareOrderModel model = listShare.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, model.getSd_id());
            UIHelper.startActivity(getActivity(), GoodsDetailsActivity.class, bundle);
        });
    }

    /**
     * 加载已晒单数据
     */
    private void loadShareData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.USER_SHARE_LIST, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (pageIndex == 1) {
                    xRefreshView.stopRefresh();
                }
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<UserShareModel.ShareOrderModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    UserShareModel model = JSON.parseObject(message.getData(), UserShareModel.class);
                    list = model.getLists();
                    listShare.addAll(list);
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

            }
        });
    }

    /**
     * 加载未晒单的商品信息
     */
    private void loadGoods() {
        this.listGoods.clear();
        this.bindGoodsRecycleView();
        this.bindGoodsListener();
        this.loadGoodsData();
    }

    /**
     * 绑定未晒单的商品
     */
    private void bindGoodsRecycleView() {
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.goodsAdapter = new RecyclerAdapter<UserShareModel.ShareOrderModel>(this.getActivity(), listGoods, R.layout.item_share_goods) {
            @Override
            public void convert(RecyclerViewHolder helper, UserShareModel.ShareOrderModel item, int position) {
                helper.setText(R.id.tv_title, item.getTitle());
                helper.setText(R.id.tv_q_end_time, MessageFormat.format("揭晓时间：{0}", DateHelper.getDefaultDate(new Date(item.getQ_end_time() * 1000))));
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.iv_goods_img, imgUrl);
                helper.setText(R.id.tv_q_user_code, MessageFormat.format("幸运号码：{0}", item.getQ_user_code()));
                Button btn_share = helper.getView(R.id.btn_share);
                btn_share.setOnClickListener((view) -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(AddShareActivity.SHARE_GOODS_TAG, item);
                    UIHelper.startActivity(getActivity(), AddShareActivity.class, bundle);
                });
            }
        };
        this.rv_share.setHasFixedSize(true);
        this.rv_share.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.rv_share.setAdapter(this.goodsAdapter);
        this.goodsAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this.getActivity()));
    }

    /**
     * 绑定未晒单的商品事件
     */
    private void bindGoodsListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    listGoods.clear();
                    loadGoodsData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadGoodsData();
            }
        });
        this.goodsAdapter.setOnItemClickListener((view, position) -> {
            UserShareModel.ShareOrderModel model = this.listGoods.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, model.getShopid());
            UIHelper.startActivity(getActivity(), GoodsDetailsActivity.class, bundle);
        });
        this.xRefreshView.setAutoRefresh(true);
    }

    /**
     * 加载未晒单的商品数据
     */
    private void loadGoodsData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.USER_UN_SHARE_LIST, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (pageIndex == 1) {
                    xRefreshView.stopRefresh();
                }
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<UserShareModel.ShareOrderModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    UserShareModel model = JSON.parseObject(message.getData(), UserShareModel.class);
                    list = model.getLists();
                    listGoods.addAll(list);
                    goodsAdapter.notifyDataSetChanged();
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

            }
        });
    }
}
