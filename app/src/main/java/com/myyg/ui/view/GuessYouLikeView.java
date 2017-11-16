package com.myyg.ui.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.alibaba.fastjson.JSON;
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
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiyuankao on 2016/6/8.
 */
public class GuessYouLikeView extends LinearLayout {
    private Context mContext;

    private RecyclerView rv_like;

    private List<GoodsModel> listGoods = new ArrayList<>();

    private RecyclerAdapter<GoodsModel> adapter;

    private String baseUrl = CommonHelper.getStaticBasePath();

    public GuessYouLikeView(Context context) {
        this(context, null);
    }

    public GuessYouLikeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuessYouLikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.initView();
    }

    /**
     *
     */
    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.view_guess_you_like, null, false);
        addView(layout);
        LinearLayout ll_wrap = (LinearLayout) layout.findViewById(R.id.ll_wrap);
        LayoutParams layoutParams = (LayoutParams) ll_wrap.getLayoutParams();
        layoutParams.width = BaseApplication.WINDOW_WIDTH;
        ll_wrap.setLayoutParams(layoutParams);
        this.rv_like = (RecyclerView) layout.findViewById(R.id.rv_like);
        this.bindRecyclerView();
        this.loadData();
    }

    /**
     *
     */
    private void bindRecyclerView() {
        this.adapter = new RecyclerAdapter<GoodsModel>(this.mContext, this.listGoods, R.layout.item_guess_you_like) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.iv_goods_img, imgUrl);
                helper.setText(R.id.tv_goods_name, item.getTitle());
                RoundCornerProgressBar rc_schedule = helper.getView(R.id.rc_schedule);
                rc_schedule.setMax(100);
                rc_schedule.setProgress(item.getCanyurenshu() * 100 / item.getZongrenshu());
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
                if (item.getYunjiage() > 1) {
                    iv_price.setVisibility(View.VISIBLE);
                }
            }
        };
        this.rv_like.setHasFixedSize(true);
        this.rv_like.setLayoutManager(new GridLayoutManager(this.mContext, 1, LinearLayoutManager.HORIZONTAL, false));
        this.rv_like.setAdapter(this.adapter);
        this.adapter.setOnItemClickListener((view, position) -> {
            Bundle bundle = new Bundle();
            GoodsModel model = listGoods.get(position);
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, model.getId());
            UIHelper.startActivity((Activity) this.mContext, GoodsDetailsActivity.class, bundle);
        });
    }

    /**
     *
     */
    private void loadData() {
        this.listGoods.clear();
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.GOODS_RANDOM, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    List<GoodsModel> list = JSON.parseArray(message.getData(), GoodsModel.class);
                    listGoods.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }
}
