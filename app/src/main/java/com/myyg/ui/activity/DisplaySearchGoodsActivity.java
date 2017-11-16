package com.myyg.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.myyg.model.GoodsCategoryModel;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/24.
 */
public class DisplaySearchGoodsActivity extends BaseActivity {

    public final static String KEYWORD = "goods_key_word";

    private String key_word;

    private XRefreshView xRefreshView;

    private RecyclerView rv_good;

    private RecyclerAdapter<GoodsModel> adapter;

    private List<GoodsModel> listGoods = new ArrayList<>();

    private GoodsCategoryModel goodsCategory;

    private String baseUrl = CommonHelper.getStaticBasePath();

    private int pageIndex = 1;
    private TextView tv_total;

    @Override
    public void initView() {
        setContentView(R.layout.activity_display_search_goods);
        tv_total = (TextView) findViewById(R.id.tv_total);
        this.rv_good = (RecyclerView) findViewById(R.id.rv_good);
        this.xRefreshView = (XRefreshView) findViewById(R.id.xRefreshView);

    }

    @Override
    public void initData() {
        this.key_word = this.getIntent().getStringExtra(KEYWORD);
    }

    @Override
    public void fillView() {
        setToolBar("搜索  " + key_word);
        this.bindRecycleView();
        loadData();
    }


    private void bindRecycleView() {
        this.listGoods.clear();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(DisplaySearchGoodsActivity.this);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.adapter = new RecyclerAdapter<GoodsModel>(DisplaySearchGoodsActivity.this, this.listGoods, R.layout.item_specific_commodity) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.iv_thumb, imgUrl);
                helper.setText(R.id.tv_shenyurenshu, item.getShenyurenshu() + "");
                helper.setText(R.id.tv_title, item.getTitle());
                helper.setText(R.id.tv_zongrenshu, MessageFormat.format("总需：{0}", item.getZongrenshu()));
            }
        };
        this.rv_good.setHasFixedSize(true);
        this.rv_good.setLayoutManager(new GridLayoutManager(DisplaySearchGoodsActivity.this, 1, LinearLayoutManager.VERTICAL, false));
        this.rv_good.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(this.mContext));

    }

    private void loadData() {
        UIHelper.showLoading(DisplaySearchGoodsActivity.this);
        String url = MessageFormat.format("{0}/{1}/{2}/{3}", URLS.GOODS_SEARCH,key_word, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        xRefreshView.stopRefresh();
                        UIHelper.toastMessage(mContext, message.getMsg());
                        return;
                    }
                    List<GoodsModel> list = JSON.parseArray(message.getData(), GoodsModel.class);
                    listGoods.addAll(list);
                    adapter.notifyDataSetChanged();
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
                } catch (Exception ex) {

                } finally {
                    UIHelper.hideLoading(1);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });
    }

}
