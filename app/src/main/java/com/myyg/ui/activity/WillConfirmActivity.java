package com.myyg.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.myyg.common.ImageLoaderEx;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.AddressModel;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.model.PrizeStatusModel;
import com.myyg.model.WillConfirmModel;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/6/10.
 */
public class WillConfirmActivity extends BaseActivity {
    public static final String GOODS_ID_CODE = "goods_id_code";
    public static final String GOODS_MODEL_CODE = "goods_model_code";
    private static final int ADDRESS_ADD_REQUEST_CODE = 0x01;
    private TextView tv_state_title, tv_company, tv_company_code, tv_shouhuoren,
            tv_mobile, tv_sheng, tv_shi, tv_xian, tv_jiedao, tv_goods_title, tv_qishu, tv_zongrenshu, tv_win_code, tv_gonumber, tv_end_time;

    private ImageView image_thumb;

    private XRefreshView xRefreshView;

    private XScrollView sv_wrap;

    private RecyclerAdapter<PrizeStatusModel> adapter;

    private RecyclerView rv_state;

    private List<PrizeStatusModel> listPrizeStatus;

    private String baseUrl = CommonHelper.getStaticBasePath();

    private int goodsId;

    private LinearLayout ll_no_address;

    private LinearLayout ll_address;

    private ImageView iv_price;

    private GoodsModel goods;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_will_confirm);
        this.rv_state = (RecyclerView) findViewById(R.id.rv_state);
        this.xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        this.sv_wrap = (XScrollView) findViewById(R.id.sv_wrap);
        this.tv_state_title = (TextView) findViewById(R.id.tv_state_title);
        this.tv_company = (TextView) findViewById(R.id.tv_company);
        this.tv_company_code = (TextView) findViewById(R.id.tv_company_code);
        this.tv_shouhuoren = (TextView) findViewById(R.id.tv_shouhuoren);
        this.tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        this.tv_sheng = (TextView) findViewById(R.id.tv_sheng);
        this.tv_shi = (TextView) findViewById(R.id.tv_shi);
        this.tv_xian = (TextView) findViewById(R.id.tv_xian);
        this.tv_jiedao = (TextView) findViewById(R.id.tv_jiedao);
        this.tv_goods_title = (TextView) findViewById(R.id.tv_goods_title);
        this.tv_qishu = (TextView) findViewById(R.id.tv_qishu);
        this.tv_zongrenshu = (TextView) findViewById(R.id.tv_zongrenshu);
        this.tv_win_code = (TextView) findViewById(R.id.tv_win_code);
        this.tv_gonumber = (TextView) findViewById(R.id.tv_gonumber);
        this.tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        this.image_thumb = (ImageView) findViewById(R.id.image_thumb);
        this.ll_no_address = (LinearLayout) this.findViewById(R.id.ll_no_address);
        this.ll_address = (LinearLayout) this.findViewById(R.id.ll_address);
        this.iv_price = (ImageView) this.findViewById(R.id.iv_price);
    }

    @Override
    public void initData() {
        this.goodsId = this.getIntent().getIntExtra(GOODS_ID_CODE, 0);
        this.goods = (GoodsModel) this.getIntent().getSerializableExtra(GOODS_MODEL_CODE);
        this.listPrizeStatus = new ArrayList<>();
        this.listPrizeStatus.add(new PrizeStatusModel("获得奖品", "", false));
        this.listPrizeStatus.add(new PrizeStatusModel("确认收货地址", "", false));
        this.listPrizeStatus.add(new PrizeStatusModel("奖品派发", "", false));
        this.listPrizeStatus.add(new PrizeStatusModel("确认收货", "", false));
        this.listPrizeStatus.add(new PrizeStatusModel("已签收", "", false));
    }

    @Override
    public void fillView() {
        this.setToolBar("中奖确认");
        this.bindLikeRecycleView();
    }

    /**
     *
     */
    private void bindLikeRecycleView() {
        this.xRefreshView.setPullLoadEnable(false);
        MyygRefreshHeader header = new MyygRefreshHeader(WillConfirmActivity.this);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setAutoRefresh(true);
        this.adapter = new RecyclerAdapter<PrizeStatusModel>(WillConfirmActivity.this, this.listPrizeStatus, R.layout.item_will_confirm) {
            @Override
            public void convert(RecyclerViewHolder helper, PrizeStatusModel item, int position) {
                helper.setText(R.id.tv_title, item.getTitle());
                if (position == 0) {
                    View view_top = helper.getView(R.id.view_top);
                    view_top.setVisibility(View.GONE);
                    ImageView iv_status = helper.getView(R.id.iv_status);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iv_status.getLayoutParams();
                    layoutParams.topMargin = CommonHelper.dp2px(mContext, 15);
                    iv_status.setLayoutParams(layoutParams);
                }
                if (position == listPrizeStatus.size() - 1) {
                    View view_bottom = helper.getView(R.id.view_bottom);
                    View view_below = helper.getView(R.id.view_below);
                    view_bottom.setVisibility(View.GONE);
                    view_below.setVisibility(View.GONE);
                }
                String time = "";
                if (!TextUtils.isEmpty(item.getTime())) {
                    time = DateHelper.getYYYYMMMDDHHMM(new Date(Long.parseLong(item.getTime()) * 1000));
                }
                helper.setText(R.id.tv_time, time);
                ImageView iv_status = helper.getView(R.id.iv_status);
                iv_status.setImageResource(R.mipmap.ic_banner_checked);
                if (item.isSelected()) {
                    iv_status.setImageResource(R.mipmap.ic_dot);
                }
            }
        };
        this.rv_state.setHasFixedSize(true);
        this.rv_state.setLayoutManager(new GridLayoutManager(WillConfirmActivity.this, 1, LinearLayoutManager.VERTICAL, false));
        this.rv_state.setAdapter(this.adapter);
        this.sv_wrap.smoothScrollTo(0, 0);
    }

    @Override
    public void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> loadData(), 1000);
            }
        });
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}", URLS.USER_WIN_DETAIL, this.goodsId);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        UIHelper.toastMessage(mContext, message.getMsg());
                        return;
                    }
                    WillConfirmModel model = JSON.parseObject(message.getData(), WillConfirmModel.class);
                    bindView(model);
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {
                    xRefreshView.stopRefresh();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });

    }

    /**
     * @param willConfirmModel
     */
    private void bindView(WillConfirmModel willConfirmModel) {
        tv_state_title.setText(willConfirmModel.getTitle());
        tv_company.setText("物流公司：" + willConfirmModel.getCompany());
        tv_company_code.setText("运单号码：" + willConfirmModel.getCompany_code());
        String avatar = MessageFormat.format("{0}/{1}", this.baseUrl, willConfirmModel.getThumb());
        ImageLoader.getInstance().displayImage(avatar, this.image_thumb, ImageLoaderEx.getDisplayImageOptions());
        tv_goods_title.setText(willConfirmModel.getTitle());
        tv_qishu.setText("期号：" + willConfirmModel.getQishu());
        tv_zongrenshu.setText("总需：" + willConfirmModel.getZongrenshu() + "人次");
        String win_code = MessageFormat.format("幸运号码：<font color=\"#f85767\">{0}</font>", willConfirmModel.getWin_code());
        tv_win_code.setText(Html.fromHtml(win_code));
        tv_gonumber.setText("本期参与：" + willConfirmModel.getGonumber() + "人次");
        String time;
        time = DateHelper.getDefaultDate(new Date(Long.parseLong(willConfirmModel.getQ_end_time()) * 1000));
        tv_end_time.setText("揭晓时间：" + time);
        AddressModel address = willConfirmModel.getAddress();
        if (address != null && !TextUtils.isEmpty(address.getSheng())) {
            this.ll_no_address.setVisibility(View.GONE);
            this.ll_address.setVisibility(View.VISIBLE);
            tv_sheng.setText(address.getSheng());
            tv_shi.setText(address.getShi());
            tv_xian.setText(address.getXian());
            tv_jiedao.setText(address.getJiedao());
        } else {
            this.ll_no_address.setVisibility(View.VISIBLE);
            this.ll_address.setVisibility(View.GONE);
        }
        tv_shouhuoren.setText(willConfirmModel.getAddress().getShouhuoren());
        tv_mobile.setText(willConfirmModel.getAddress().getMobile());
        this.handlerPrizeStatus(willConfirmModel);
        this.adapter.notifyDataSetChanged();
        this.iv_price.setVisibility(View.GONE);
        if (this.goods.getYunjiage() > 1) {
            this.iv_price.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param model
     */
    private void handlerPrizeStatus(WillConfirmModel model) {
        listPrizeStatus.get(0).setTime(model.getQ_end_time());
        listPrizeStatus.get(1).setTime(model.getAddress_time());
        listPrizeStatus.get(2).setTime(model.getSend_time());
        listPrizeStatus.get(3).setTime(model.getReceive_time());
        for (PrizeStatusModel item : listPrizeStatus) {
            item.setSelected(false);
        }
        if (!TextUtils.isEmpty(model.getReceive_time())) {
            listPrizeStatus.get(listPrizeStatus.size() - 1).setSelected(true);
            return;
        }
        if (!TextUtils.isEmpty(model.getSend_time())) {
            listPrizeStatus.get(2).setSelected(true);
            return;
        }
        if (!TextUtils.isEmpty(model.getAddress_time())) {
            listPrizeStatus.get(1).setSelected(true);
            return;
        }
        listPrizeStatus.get(0).setSelected(true);
    }

    /**
     * 中奖界面跳转详情界面
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.image_thumb:
//                Bundle ivBundle = new Bundle();
//                ivBundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, goodsId);
//                UIHelper.startActivity(mContext, GoodsDetailsActivity.class, ivBundle);
//                break;
//            case R.id.tv_goods_title:
//                Bundle tvBundle = new Bundle();
//                tvBundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, goodsId);
//                UIHelper.startActivity(mContext, GoodsDetailsActivity.class, tvBundle);
//                break;
            case R.id.ll_no_address:
                UIHelper.startActivityForResult(this.mContext, AddAddressActivity.class, ADDRESS_ADD_REQUEST_CODE, null);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ADDRESS_ADD_REQUEST_CODE:
                this.loadData();
                break;
        }
    }
}
