package com.myyg.ui.activity;

import android.support.v7.widget.RecyclerView;

import com.andview.refreshview.XRefreshView;
import com.myyg.R;
import com.myyg.adapter.recycler.RecyclerAdapter;
import com.myyg.base.BaseActivity;
import com.myyg.model.GoodsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/31.
 */
public class IntegralDetailActivity extends BaseActivity {
    private XRefreshView xRefreshView;

    private RecyclerView rv_integral_datail;

    private RecyclerAdapter<GoodsModel> adapter;

    private List<GoodsModel> listGoods = new ArrayList<>();

    @Override
    public void initView() {
        setContentView(R.layout.activity_integral_detail);
        this.rv_integral_datail = (RecyclerView) findViewById(R.id.rv_integral_datail);
        this.xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        setToolBar("佣金明细");

    }
}
