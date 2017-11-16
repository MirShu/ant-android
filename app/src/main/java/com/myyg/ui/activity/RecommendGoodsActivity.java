package com.myyg.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.db.DbHelper;
import com.myyg.model.GoodsListModel;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.ui.view.AnimationDialog;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.viewpagerindicator.BadgeView;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.rey.material.widget.Button;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class RecommendGoodsActivity extends BaseActivity {
    private XRefreshView xRefreshView;

    private RecyclerView rv_recommend;

    private int pageIndex = 1;

    private List<GoodsModel> listGoods = new ArrayList<>();

    private RecyclerAdapter<GoodsModel> goodsAdapter;

    private String baseUrl = CommonHelper.getStaticBasePath();

    private BadgeView badgeView;

    private Toolbar bar_title;

    private TextView tv_total;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_recommend_goods);
        this.bar_title = (Toolbar) this.findViewById(R.id.bar_title);
        this.xRefreshView = (XRefreshView) this.findViewById(R.id.xRefreshView);
        this.rv_recommend = (RecyclerView) this.findViewById(R.id.rv_recommend);
        this.tv_total = (TextView) this.findViewById(R.id.tv_total);
    }

    @Override
    public void initData() {
        int px = CommonHelper.dp2px(this.mContext, 6);
        this.badgeView = new BadgeView(mContext, bar_title);
        this.badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        this.badgeView.setBadgeMargin(px, px);
        this.badgeView.setBadgeBackgroundColor(R.color.color_000000);
        this.showShopCartNumber(0);
    }

    @Override
    public void fillView() {
        this.setToolBar("推荐商品");
        this.goodsAdapter = new RecyclerAdapter<GoodsModel>(this.mContext, this.listGoods, R.layout.item_recommend_goods) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsModel item, int position) {
                ImageView iv_thumb = helper.getView(R.id.iv_thumb);
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.iv_thumb, imgUrl);
                helper.setText(R.id.tv_shenyurenshu, Html.fromHtml(MessageFormat.format("剩余：<font color=\"#0171bb\">{0}</font>", String.valueOf(item.getShenyurenshu()))));
                helper.setText(R.id.tv_title, item.getTitle());
                helper.setText(R.id.tv_zongrenshu, MessageFormat.format("总需：{0}", String.valueOf(item.getZongrenshu())));
                RoundCornerProgressBar rc_schedule = helper.getView(R.id.rc_schedule);
                rc_schedule.setMax(100);
                rc_schedule.setProgress(item.getCanyurenshu() * 100 / item.getZongrenshu());
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
                if (item.getYunjiage() > 1) {
                    iv_price.setVisibility(View.VISIBLE);
                }
                Button btn_join_list = helper.getView(R.id.btn_join_list);
                btn_join_list.setOnClickListener(v -> {
                    int[] location = new int[2];
                    iv_thumb.getLocationOnScreen(location);
                    int width = iv_thumb.getWidth();
                    addShopCart(item, location, width);
                });
            }
        };
        MyygRefreshHeader header = new MyygRefreshHeader(this.mContext);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setPullLoadEnable(true);
        this.xRefreshView.setMoveForHorizontal(false);
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                new Handler().postDelayed(() -> loadData(), 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }
        });
        this.xRefreshView.setAutoLoadMore(true);
        this.xRefreshView.setAutoRefresh(true);
        this.rv_recommend.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rv_recommend.setAdapter(this.goodsAdapter);
        this.goodsAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this.mContext));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_shopcart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shopcart:
                Intent intent = new Intent();
                intent.setAction(SysConstant.ACTION_MYYG_RECEIVE_SHOP_CART);
                this.sendBroadcast(intent);
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.GOODS_RECOMMEND_LIST, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
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
                    listGoods.clear();
                }
                GoodsListModel model = JSON.parseObject(message.getData(), GoodsListModel.class);
                tv_total.setText(MessageFormat.format("共{0}件商品", String.valueOf(model.getTotal())));
                List<GoodsModel> list = model.getList();
                listGoods.addAll(list);
                goodsAdapter.notifyDataSetChanged();
                if (list.size() < SysConstant.PAGE_SIZE) {
                    xRefreshView.setLoadComplete(true);
                } else {
                    xRefreshView.setLoadComplete(false);
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

    /**
     * @param model
     * @param location
     * @param width
     */
    private void addShopCart(GoodsModel model, int[] location, int width) {
        DbHelper.addShopCart(model);
        int targetX = BaseApplication.WINDOW_WIDTH - CommonHelper.dp2px(this.mContext, 40);
        int targetY = CommonHelper.getStatusBarHeight(this.mContext) - CommonHelper.dp2px(this.mContext, 40);
        int[] targetLocation = new int[]{targetX, targetY};
        AnimationDialog dialog = new AnimationDialog(this.mContext, model, location, width, targetLocation);
        dialog.show();
        this.showShopCartNumber(1000);
    }

    /**
     *
     */
    private void showShopCartNumber(long delayMillis) {
        new Handler().postDelayed(() -> {
            int count = DbHelper.getTotalShopCart();
            this.badgeView.setText(count + "");
            this.badgeView.show();
            if (count == 0) {
                this.badgeView.hide();
            }
        }, delayMillis);
    }
}
