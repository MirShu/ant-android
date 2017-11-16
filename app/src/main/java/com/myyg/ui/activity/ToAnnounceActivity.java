package com.myyg.ui.activity;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

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
import com.myyg.constant.SysConstant;
import com.myyg.constant.URLS;
import com.myyg.model.GoodsCategoryModel;
import com.myyg.model.GoodsPastModel;
import com.myyg.model.MessageResult;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/29.
 */
public class ToAnnounceActivity extends BaseActivity {
    public final static String GOODS_CATEGORY_TAG = "goods_category_tag";

    private GoodsCategoryModel goodsCategory;

    private int pageIndex = 1;

    private XRefreshView xRefreshView;

    private RecyclerView rv_to_announce;

    private RecyclerAdapter<GoodsPastModel> adapter;

    private List<GoodsPastModel> listGoods = new ArrayList<>();

    @Override
    public void initView() {
        setContentView(R.layout.activity_to_announce);
        this.rv_to_announce = (RecyclerView) findViewById(R.id.rv_to_announce);
        this.xRefreshView = (XRefreshView) findViewById(R.id.xRefreshView);
        this.bindRecycleView();
    }

    @Override
    public void initData() {
        this.goodsCategory = (GoodsCategoryModel) getIntent().getSerializableExtra(GOODS_CATEGORY_TAG);
    }

    @Override
    public void fillView() {
        setToolBar("往期揭晓");

    }

    private void bindRecycleView() {
        this.listGoods.clear();
        this.loadData();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(ToAnnounceActivity.this);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.adapter = new RecyclerAdapter<GoodsPastModel>(ToAnnounceActivity.this, this.listGoods, R.layout.item_to_announce) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsPastModel item, int position) {

            }
        };
    }

    /**
     * 绑定监听事件
     */
    public void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().post(() -> {
                    pageIndex = 1;
                    listGoods.clear();
                    loadData();
                });
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }
        });
//        this.adapter.setOnItemClickListener((parent, position) -> UIHelper.startActivity(SpecificCommodityActivity.this, GoodsDetailsActivity.class));
    }

    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}/{3}", URLS.GOODS_LAST_LOTTERY, 6, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<GoodsPastModel> list = new ArrayList<>();
//                if (message.getCode() == SysStatusCode.SUCCESS) {
//                    GoodsPastListModel goods = JSON.parseObject(message.getData(), GoodsPastListModel.class);
//                    list = goods.getList();
//                    listGoods.addAll(list);
//                    adapter.notifyDataSetChanged();
//                }
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
