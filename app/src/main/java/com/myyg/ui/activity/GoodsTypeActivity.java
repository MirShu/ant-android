package com.myyg.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XScrollView;
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
import com.myyg.constant.SharedKeys;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.GoodsCategoryModel;
import com.myyg.model.MessageResult;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.SharedHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.CustomGifHeader;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class GoodsTypeActivity extends BaseActivity {
    private static final int GOODS_GO_SHOP_REQUEST_CODE = 0x01;

    private List<GoodsCategoryModel> listGoodsCategory = new ArrayList<>();

    private RecyclerAdapter<GoodsCategoryModel> adapter;

    private XRefreshView xRefreshView;

    private XScrollView sv_wrap;

    private RecyclerView rv_goods_type;

    private String baseUrl = CommonHelper.getStaticBasePath();

    @Override
    public void initView() {
        setContentView(R.layout.activity_goods_type);
        this.xRefreshView = (XRefreshView) this.findViewById(R.id.xrefreshview);
        this.sv_wrap = (XScrollView) this.findViewById(R.id.sv_wrap);
        this.rv_goods_type = (RecyclerView) this.findViewById(R.id.rv_goods_type);
    }

    @Override
    public void initData() {
        this.loadData();
    }

    @Override
    public void fillView() {
        this.setToolBar("分类浏览");
        this.bindRecycleView();
        this.sv_wrap.smoothScrollTo(0, 0);
    }

    /**
     *
     */
    private void bindRecycleView() {
        UIHelper.showLoading(mContext);
        this.adapter = new RecyclerAdapter<GoodsCategoryModel>(this.mContext, this.listGoodsCategory, R.layout.item_goods_type) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsCategoryModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getUrl());
                helper.setImageUrl(R.id.iv_goods_type, imgUrl);
                String name = item.getName();
                helper.setText(R.id.tv_goods_type_name, name);
            }
        };
        this.adapter.setOnItemClickListener((view, position) -> {
            UIHelper.toastMessage(mContext, listGoodsCategory.get(position).getName());
        });
        this.rv_goods_type.setHasFixedSize(true);
        this.rv_goods_type.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rv_goods_type.setAdapter(this.adapter);
        MyygRefreshHeader header = new MyygRefreshHeader(this.mContext);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    loadData();
                }, 1000);
            }
        });
        this.adapter.setOnItemClickListener((parent, position) -> {
            GoodsCategoryModel model = listGoodsCategory.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable(SpecificCommodityActivity.GOODS_CATEGORY_TAG, model);
            UIHelper.startActivityForResult(this.mContext, SpecificCommodityActivity.class, GOODS_GO_SHOP_REQUEST_CODE, bundle);
        });
    }

    /**
     *
     */
    private void loadData() {
        this.listGoodsCategory.clear();
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.EXTRA_CATEGORY, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                UIHelper.hideLoading(2);
                xRefreshView.stopRefresh();
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    List<GoodsCategoryModel> list = JSON.parseArray(message.getData(), GoodsCategoryModel.class);
                    listGoodsCategory.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
                xRefreshView.stopRefresh();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_goods_all:
                UIHelper.startActivityForResult(mContext, GoodsActivity.class, GOODS_GO_SHOP_REQUEST_CODE, null);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case GOODS_GO_SHOP_REQUEST_CODE:
                this.finish();
                break;
        }
    }
}
