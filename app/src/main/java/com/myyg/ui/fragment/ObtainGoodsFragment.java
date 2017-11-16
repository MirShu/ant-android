package com.myyg.ui.fragment;

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
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.ui.activity.HisPersonalCenterActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObtainGoodsFragment extends Fragment {
    private final static String TAG = ObtainGoodsFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";

    private String mUserId;

    private XRefreshView xRefreshView;

    private RecyclerView rv_obtain_goods;

    private RecyclerAdapter<GoodsModel> adapter;

    private List<GoodsModel> listGoods = new ArrayList<>();

    private int pageIndex = 1;

    private String baseUrl = CommonHelper.getStaticBasePath();

    public ObtainGoodsFragment() {

    }

    public static ObtainGoodsFragment newInstance(String user_id) {
        ObtainGoodsFragment fragment = new ObtainGoodsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_obtain_goods, container, false);
        this.xRefreshView = (XRefreshView) view.findViewById(R.id.xRefreshView);
        this.rv_obtain_goods = (RecyclerView) view.findViewById(R.id.rv_obtain_goods);
        this.bindRecycleView();
        this.bindListener();
        return view;
    }

    /**
     *
     */
    private void bindRecycleView() {
        this.listGoods.clear();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.xRefreshView.setAutoRefresh(true);
        this.adapter = new RecyclerAdapter<GoodsModel>(this.getActivity(), this.listGoods, R.layout.item_obtain_goods) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.iv_thumb, imgUrl);
                helper.setText(R.id.tv_title, item.getTitle());
                String win_code = MessageFormat.format("幸运码：<font color=\"#f85767\">{0}</font>", item.getWin_code());
                helper.setText(R.id.tv_win_code, Html.fromHtml(win_code));
                String time = DateHelper.getDefaultDate(new Date(Long.parseLong(item.getWin_time()) * 1000));
                helper.setText(R.id.tv_win_time, "揭晓时间：" + time);
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
                if (item.getYunjiage() > 1) {
                    iv_price.setVisibility(View.VISIBLE);
                }
            }
        };
        this.rv_obtain_goods.setHasFixedSize(true);
        this.rv_obtain_goods.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.rv_obtain_goods.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(this.getActivity()));
    }

    /**
     *
     */
    private void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                xRefreshView.setPullLoadEnable(true);
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
        this.adapter.setOnItemClickListener((parent, position) ->
        {
            GoodsModel model = listGoods.get(position);
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
        UIHelper.startActivityForResult(this.getActivity(), GoodsDetailsActivity.class, HisPersonalCenterActivity.GOODS_GO_SHOP_REQUEST_CODE, bundle);
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}/{3}", URLS.MEMBER_WIN_LIST, this.mUserId, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<GoodsModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    list = JSON.parseArray(message.getData(), GoodsModel.class);
                    listGoods.addAll(list);
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
