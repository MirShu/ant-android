package com.myyg.ui.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

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
import com.myyg.adapter.CountdownAdapter;
import com.myyg.adapter.recycler.RecyclerAdapter;
import com.myyg.adapter.recycler.RecyclerViewHolder;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.ui.activity.GoodsTypeActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.CustomGifHeader;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AnnouncedFragment extends Fragment {
    private List<GoodsModel> listGoods = new ArrayList<>();

    private XRefreshView xRefreshView;

    private CountdownAdapter<GoodsModel> adapter;

    private RecyclerView rv_announced;

    private int pageIndex = 1;

    private String baseUrl = CommonHelper.getStaticBasePath();

    /**
     *
     */
    public AnnouncedFragment() {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announced, container, false);
        TextView tv_head_title = (TextView) view.findViewById(R.id.tv_head_title);
        tv_head_title.setText("限时揭晓");
        this.xRefreshView = (XRefreshView) view.findViewById(R.id.xrefreshview);
        this.rv_announced = (RecyclerView) view.findViewById(R.id.rv_announced);
        ImageView iv_more = (ImageView) view.findViewById(R.id.iv_more);
        iv_more.setOnClickListener((v) -> {
            UIHelper.startActivity(this.getActivity(), GoodsTypeActivity.class);
        });
        this.bindRecycleView();
        return view;

    }

    /**
     *
     */
    private void bindRecycleView() {
        this.adapter = new CountdownAdapter<GoodsModel>(this.getActivity(), this.listGoods, R.layout.item_announced) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.iv_goods_img, imgUrl);
                helper.setText(R.id.tv_goods_name, item.getTitle());
                helper.setText(R.id.tv_qishu, MessageFormat.format("期号：{0}", item.getQishu()));
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
                if (item.getYunjiage() > 1) {
                    iv_price.setVisibility(View.VISIBLE);
                }
                LinearLayout ll_announced = helper.getView(R.id.ll_announced);
                ll_announced.setVisibility(View.GONE);
                LinearLayout ll_winning_user = helper.getView(R.id.ll_winning_user);
                ll_winning_user.setVisibility(View.GONE);
                if (item.getQ_showtime().equals("Y") && item.getCountdown() > 0) {
                    ll_announced.setVisibility(View.VISIBLE);
                    return;
                }
                ll_winning_user.setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_user_join_number, MessageFormat.format("参与人次：{0}", String.valueOf(item.getGonumber())));
                helper.setText(R.id.tv_q_user, Html.fromHtml(MessageFormat.format("获得用户：<font color=\"#0171bb\">{0}</font>", item.getQ_user())));
                helper.setText(R.id.tv_q_user_code, Html.fromHtml(MessageFormat.format("幸运号码：<font color=\"#c62334\">{0}</font>", item.getQ_user_code())));
                helper.setText(R.id.tv_q_end_time, MessageFormat.format("揭晓时间：{0}", DateHelper.getCustomerDate(item.getQ_end_time() * 1000)));
            }
        };
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setPullLoadEnable(true);
        this.xRefreshView.setMoveForHorizontal(false);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));
        this.rv_announced.setHasFixedSize(true);
        this.rv_announced.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        this.rv_announced.setAdapter(adapter);
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    listGoods.clear();
                    loadData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }
        });
        this.adapter.setOnItemClickListener((parent, position) -> {
            GoodsModel goods = listGoods.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, goods.getId());
            UIHelper.startActivity(getActivity(), GoodsDetailsActivity.class, bundle);
        });
        this.loadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible()) {
            //UIHelper.showLoading(getActivity());
            //loadData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.GOODS_LATEST_LIST, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                UIHelper.hideLoading();
                if (pageIndex == 1) {
                    xRefreshView.stopRefresh();
                    listGoods.clear();
                }
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<GoodsModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    list = JSON.parseArray(message.getData(), GoodsModel.class);
                    long currentTimeMillis = System.currentTimeMillis();
                    for (GoodsModel item : list) {
                        if (item.getQ_showtime().equals("Y") && item.getCountdown() > 0) {
                            item.setEndTime(currentTimeMillis + item.getCountdown() * 1000);
                        }
                    }
                    listGoods.addAll(list);
                    adapter.notifyDataSetChanged();
                }
                if (list.size() < SysConstant.PAGE_SIZE) {
                    xRefreshView.setPullLoadEnable(false);
                }
                if (pageIndex > 1) {
                    xRefreshView.stopLoadMore();
                }
                if (list.size() == SysConstant.PAGE_SIZE) {
                    pageIndex++;
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
            xRefreshView.stopLoadMore();
        }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != this.adapter) {
            this.adapter.startRefreshTime();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != this.adapter) {
            this.adapter.cancelRefreshTime();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != this.adapter) {
            this.adapter.cancelRefreshTime();
        }
    }
}
