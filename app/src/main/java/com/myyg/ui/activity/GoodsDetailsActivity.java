package com.myyg.ui.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.alibaba.fastjson.JSON;
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
import com.myyg.banner.CommonBanner;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.common.NetworkImageViewHolder;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysEnums;
import com.myyg.constant.SysHtml;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.db.DbHelper;
import com.myyg.model.GoodsDetailsModel;
import com.myyg.model.MessageResult;
import com.myyg.model.ShopRecordsModel;
import com.myyg.ui.view.JoinPopupWindow;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.widget.RoundedImageView;
import com.myyg.widget.viewpagerindicator.BadgeView;
import com.myyg.widget.xrefreshview.MyygRefreshFooter;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;

public class GoodsDetailsActivity extends BaseActivity {
    public static final String GOODS_ID_TAG = "goods_id_tag";
    public static final int GOODS_GO_SHOP_REQUEST_CODE = 0x01;
    private CommonBanner cb_banner;
    private List<String> networkImages;
    private XRefreshView xRefreshView;
    private RecyclerAdapter<ShopRecordsModel> adapter;
    private int goodsId;
    private GoodsDetailsModel goods;
    private String[] images = {};
    private RelativeLayout rl_wrap;
    private TextView tv_goods_name;
    private TextView tv_qishu;
    private RoundCornerProgressBar rpb_progress;
    private TextView tv_zongrenshu;
    private TextView tv_shenyurenshu;
    private TextView tv_title2;
    private TextView tv_status;
    private RecyclerView rv_partake_record;
    private List<ShopRecordsModel> listRecords = new ArrayList<>();
    private int pageIndex = 1;
    private String baseUrl = CommonHelper.getStaticBasePath();
    private LinearLayout ll_join;
    private CardView cv_winning;
    private RoundedImageView riv_q_img;
    private TextView tv_q_name;
    private TextView tv_q_uid;
    private TextView tv_q_qishu;
    private TextView tv_gonumber;
    private TextView tv_q_end_time;
    private TextView tv_q_user_code;
    private TextView tv_join_message, tv_next_qishu;
    private LinearLayout ll_join_record;
    private TextView tv_user_join_number;
    private TextView tv_user_join_codes;
    private LinearLayout ll_countdown;
    private CountdownView cv_goods;
    private RelativeLayout rl_join, rl_next;
    private ImageView iv_cart;
    private BadgeView badgeView;
    private ImageView iv_price;
    private RelativeLayout rl_go_next;
    private TextView tv_go_next_qishu;

    @Override
    public void initView() {
        setContentView(R.layout.activity_goods_details);
        this.cb_banner = (CommonBanner) this.findViewById(R.id.cb_banner);
        this.iv_price = (ImageView) this.findViewById(R.id.iv_price);
        this.xRefreshView = (XRefreshView) this.findViewById(R.id.xrefreshview);
        this.rl_wrap = (RelativeLayout) this.findViewById(R.id.rl_wrap);
        this.rv_partake_record = (RecyclerView) this.findViewById(R.id.rv_partake_record);
        this.tv_goods_name = (TextView) this.findViewById(R.id.tv_goods_name);
        this.tv_title2 = (TextView) this.findViewById(R.id.tv_title2);
        this.tv_qishu = (TextView) this.findViewById(R.id.tv_qishu);
        this.rpb_progress = (RoundCornerProgressBar) this.findViewById(R.id.rpb_progress);
        this.tv_zongrenshu = (TextView) this.findViewById(R.id.tv_zongrenshu);
        this.tv_shenyurenshu = (TextView) this.findViewById(R.id.tv_shenyurenshu);
        this.tv_status = (TextView) this.findViewById(R.id.tv_status);
        this.ll_join = (LinearLayout) this.findViewById(R.id.ll_join);
        this.cv_winning = (CardView) this.findViewById(R.id.cv_winning);
        this.riv_q_img = (RoundedImageView) this.findViewById(R.id.riv_q_img);
        this.tv_q_name = (TextView) this.findViewById(R.id.tv_q_name);
        this.tv_q_uid = (TextView) this.findViewById(R.id.tv_q_uid);
        this.tv_q_qishu = (TextView) this.findViewById(R.id.tv_q_qishu);
        this.tv_gonumber = (TextView) this.findViewById(R.id.tv_gonumber);
        this.tv_q_end_time = (TextView) this.findViewById(R.id.tv_q_end_time);
        this.tv_q_user_code = (TextView) this.findViewById(R.id.tv_q_user_code);
        this.tv_join_message = (TextView) this.findViewById(R.id.tv_join_message);
        this.ll_join_record = (LinearLayout) this.findViewById(R.id.ll_join_record);
        this.tv_user_join_number = (TextView) this.findViewById(R.id.tv_user_join_number);
        this.tv_user_join_codes = (TextView) this.findViewById(R.id.tv_user_join_codes);
        this.ll_countdown = (LinearLayout) this.findViewById(R.id.ll_countdown);
        this.cv_goods = (CountdownView) this.findViewById(R.id.cv_goods);
        this.rl_join = (RelativeLayout) this.findViewById(R.id.rl_join);
        this.rl_next = (RelativeLayout) this.findViewById(R.id.rl_next);
        this.tv_next_qishu = (TextView) this.findViewById(R.id.tv_next_qishu);
        this.iv_cart = (ImageView) this.findViewById(R.id.iv_cart);
        this.rl_go_next = (RelativeLayout) this.findViewById(R.id.rl_go_next);
        this.tv_go_next_qishu = (TextView) this.findViewById(R.id.tv_go_next_qishu);
    }

    @Override
    public void initData() {
        this.goodsId = getIntent().getIntExtra(GOODS_ID_TAG, 0);
    }

    @Override
    public void fillView() {
        this.setToolBar("商品详情");
        this.xRefreshView.setMoveForHorizontal(true);
        this.bindPartakeRecordRecycleView();
        this.loadData();
    }

    /**
     *
     */
    private void bindBanner() {
        for (int i = 0; i < images.length; i++) {
            String imgUrl = MessageFormat.format("{0}/{1}", this.baseUrl, images[i]);
            images[i] = imgUrl;
        }
        this.networkImages = Arrays.asList(images);
        this.cb_banner.setPages(() -> new NetworkImageViewHolder(), networkImages)
                .setPageIndicator(new int[]{R.mipmap.ic_banner_default, R.mipmap.ic_banner_checked});
    }

    /**
     *
     */
    private void bindPartakeRecordRecycleView() {
        this.adapter = new RecyclerAdapter<ShopRecordsModel>(this.mContext, listRecords, R.layout.item_partake_record) {
            @Override
            public void convert(RecyclerViewHolder helper, ShopRecordsModel item, int position) {
                View view_split = helper.getView(R.id.view_split);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view_split.getLayoutParams();
                layoutParams.height = CommonHelper.dp2px(mContext, 45);
                TextView tv_date = helper.getView(R.id.tv_date);
                tv_date.setVisibility(View.GONE);
                if (position == 0) {
//                    tv_date.setVisibility(View.VISIBLE);
//                    layoutParams.height = CommonHelper.dp2px(mContext, 80);
                }
                view_split.setLayoutParams(layoutParams);
                String ip = TextUtils.isEmpty(item.getIp()) ? "" : MessageFormat.format("({0})", item.getIp());
                String userName = MessageFormat.format("<font color=\"#0171bb\">{0}</font>{1}", item.getUsername(), ip);
                helper.setText(R.id.tv_user_name, Html.fromHtml(userName));
                String time = MessageFormat.format("<font color=\"#333333\">参与了</font><font color=\"#f95667\">{0}</font><font color=\"#333333\">人次</font> {1}", String.valueOf(item.getGonumber()), DateHelper.getDefaultDate(item.getTime() * 1000));
                helper.setText(R.id.tv_time, Html.fromHtml(time));
                String photo = MessageFormat.format("{0}/{1}?m={2}", baseUrl, item.getUphoto(), System.currentTimeMillis());
                helper.setImageUrl(R.id.riv_user_photo, photo);
            }
        };
        this.rv_partake_record.setHasFixedSize(true);
        this.rv_partake_record.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rv_partake_record.setAdapter(adapter);
        this.xRefreshView.setCustomFooterView(new MyygRefreshFooter(this.mContext));
        this.xRefreshView.setAutoLoadMore(true);
        this.loadShopRecords(pageIndex);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.rv_details_to_announce:
                bundle.putInt(PastGoodsActivity.GOODS_SID_TAG, this.goods.getSid());
                UIHelper.startActivityForResult(this.mContext, PastGoodsActivity.class, PastGoodsActivity.GOODS_GO_SHOP_REQUEST_CODE, bundle);
                break;
            case R.id.rv_details_graphic_details:
                String url = MessageFormat.format("{0}/{1}", SysHtml.HTML_GOODS_DETAILS, this.goodsId);
                bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, url);
                UIHelper.startActivity(this.mContext, WebBrowseActivity.class, bundle);
                break;
            case R.id.rv_share_order:
                bundle.putInt(GoodsShareOrderActivity.GOODS_ID_TAG, this.goodsId);
                UIHelper.startActivity(this.mContext, GoodsShareOrderActivity.class, bundle);
                break;
            case R.id.btn_go_immediately:
                bundle.putInt(GOODS_ID_TAG, this.goods.getNext_id());
                UIHelper.startActivityForResult(this.mContext, GoodsDetailsActivity.class, GOODS_GO_SHOP_REQUEST_CODE, bundle);
                this.finish();
                break;
            case R.id.iv_cart:
                this.goShopCart();
                break;
            case R.id.btn_partake:
                JoinPopupWindow window = new JoinPopupWindow(mContext, this.goods.getShenyurenshu(), joinNumber -> {
                    DbHelper.addShopCart(this.goods, joinNumber);
                    this.showShopCartNumber();
                    this.goShopCart();
                });
                window.setBackgroundDrawable(new BitmapDrawable());
                window.setFocusable(true);
                window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_join:
                DbHelper.addShopCart(this.goods, 1);
                this.showShopCartNumber();
                break;
            case R.id.btn_calculation:
                bundle = new Bundle();
                bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, MessageFormat.format("{0}/{1}", SysHtml.HTML_CALCULATION_RESULT, this.goods.getId()));
                UIHelper.startActivity(this.mContext, WebBrowseActivity.class, bundle);
                break;
            case R.id.ll_join_record:
                url = MessageFormat.format("{0}/{1}", SysHtml.HTML_BUY_DETAILS, this.goods.getId());
                bundle = new Bundle();
                bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, url);
                UIHelper.startActivity(this.mContext, WebBrowseActivity.class, bundle);
                break;
            case R.id.btn_go_next:
                bundle.putInt(GOODS_ID_TAG, this.goods.getNext_id());
                UIHelper.startActivityForResult(this.mContext, GoodsDetailsActivity.class, GOODS_GO_SHOP_REQUEST_CODE, bundle);
                this.finish();
                break;
        }
    }

    @Override
    public void bindListener() {
        MyygRefreshHeader header = new MyygRefreshHeader(this.mContext);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    listRecords.clear();
                    pageIndex = 1;
                    loadShopRecords(pageIndex);
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                new Handler().postDelayed(() -> {
                    pageIndex++;
                    loadShopRecords(pageIndex);
                }, 1000);
            }
        });
        this.cv_goods.setOnCountdownEndListener(cv -> {
            //UIHelper.toastMessage(mContext, "倒计时结束");
            this.loadData();
        });
        this.adapter.setOnItemClickListener((parent, position) -> {
            //TA的个人中心
            String user_id = listRecords.get(position).getUid();
            Bundle bundle = new Bundle();
            bundle.putString(HisPersonalCenterActivity.HIS_USER_ID, user_id);
            UIHelper.startActivityForResult(this.mContext, HisPersonalCenterActivity.class, HisPersonalCenterActivity.GOODS_GO_SHOP_REQUEST_CODE, bundle);
        });
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}", URLS.GOODS_GET_DETAILS, goodsId);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                UIHelper.showLoading(mContext);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        UIHelper.toastMessage(mContext, message.getMsg());
                        return;
                    }
                    goods = JSON.parseObject(message.getData(), GoodsDetailsModel.class);
                    showGoodsDetails();
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {
                    UIHelper.hideLoading(1);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
            }
        });
    }

    /**
     * 显示商品详细信息
     */
    private void showGoodsDetails() {
        String picArr = this.goods.getPicarr();
        if (!TextUtils.isEmpty(picArr)) {
            this.images = this.goods.getPicarr().split(";");
        }
        if (TextUtils.isEmpty(this.goods.getThumb())) {
            this.goods.setThumb("");
            if (this.images.length > 0) {
                this.goods.setThumb(this.images[0]);
            }
        }
        if (this.images.length > 0) {
            bindBanner();
        }
        if (this.goods.getYunjiage() > 1) {
            this.iv_price.setVisibility(View.VISIBLE);
        }
        this.tv_goods_name.setText(this.goods.getTitle());
        this.tv_title2.setText(this.goods.getTitle2());
        String qishu = MessageFormat.format("第{0}期", this.goods.getQishu());
        this.tv_qishu.setText(qishu);
        this.rpb_progress.setMax(100);
        this.rpb_progress.setProgress(0);
        if (this.goods.getCanyurenshu() > 0) {
            this.rpb_progress.setProgress(this.goods.getCanyurenshu() * 100 / this.goods.getZongrenshu());
        }
        String zongrenshu = MessageFormat.format("总需 {0}", String.valueOf(this.goods.getZongrenshu()));
        this.tv_zongrenshu.setText(zongrenshu);
        String shenyurenshu = MessageFormat.format("剩余 <font color=\"#0171bb\">{0}</font>", String.valueOf(this.goods.getShenyurenshu()));
        this.tv_shenyurenshu.setText(Html.fromHtml(shenyurenshu));
        if (this.goods.getStatus() == SysEnums.EnumGoodsStatus.Ongoing.getValue()) {
            this.tv_status.setVisibility(View.VISIBLE);
            this.tv_status.setText("进行中");
            this.rl_join.setVisibility(View.VISIBLE);
            this.showShopCartNumber();
        }
        this.ll_countdown.setVisibility(View.GONE);
        this.rl_go_next.setVisibility(View.GONE);
        GoodsDetailsModel.User user = this.goods.getUser();
        if (user != null) {
            // 是否显示倒计时
            String q_showtime = user.getQ_show_time();
            if (q_showtime.equals("Y") && user.getCountdown() > 0) {
                this.tv_status.setVisibility(View.VISIBLE);
                this.tv_status.setText("揭晓中");
                this.tv_status.setBackgroundResource(R.drawable.common_button_green);
                this.tv_status.setTextColor(this.getResources().getColor(R.color.color_39B44A));
                this.ll_countdown.setVisibility(View.VISIBLE);
                this.cv_goods.start(user.getCountdown() * 1000);
                this.rl_go_next.setVisibility(View.VISIBLE);
                this.tv_go_next_qishu.setText(MessageFormat.format("第{0}期 正在进行中......", this.goods.getNext_qishu()));
            }
        }
        this.cv_winning.setVisibility(View.GONE);
        if (this.goods.getStatus() == SysEnums.EnumGoodsStatus.Announced.getValue() && user != null && (!user.getQ_show_time().equals("Y") || user.getCountdown() == 0)) {
            this.cv_winning.setVisibility(View.VISIBLE);
            this.tv_status.setVisibility(View.VISIBLE);
            this.tv_status.setText("已揭晓");
            this.tv_status.setBackgroundResource(R.drawable.common_button_green);
            this.tv_status.setTextColor(this.getResources().getColor(R.color.color_39B44A));
            this.ll_join.setVisibility(View.GONE);
            String q_img = MessageFormat.format("{0}/{1}", this.baseUrl, user.getQ_img());
            ImageLoader.getInstance().displayImage(q_img, this.riv_q_img);
            this.tv_q_name.setText(MessageFormat.format("获奖者：{0}", user.getQ_name()));
            this.tv_q_uid.setText(MessageFormat.format("用户ID：{0}", user.getQ_uid()));
            this.tv_q_qishu.setText(MessageFormat.format("期号：{0}", this.goods.getQishu()));
            this.tv_gonumber.setText(MessageFormat.format("本期参与：{0}人次", String.valueOf(user.getGonumber())));
            String q_end_time = DateHelper.getDefaultDate(new Date(user.getQ_end_time() * 1000));
            this.tv_q_end_time.setText(MessageFormat.format("揭晓时间：{0}", q_end_time));
            this.tv_q_user_code.setText(MessageFormat.format("幸运码：{0}", user.getQ_user_code()));
            if (!TextUtils.isEmpty(this.goods.getNext_qishu())) {
                this.rl_next.setVisibility(View.VISIBLE);
                this.rl_join.setVisibility(View.GONE);
                this.tv_next_qishu.setText(MessageFormat.format("第{0}期 正在进行中......", this.goods.getNext_qishu()));
            }
        }
        if (this.goods.getCurrent() != null && this.goods.getCurrent().getGonumber() > 0) {
            this.ll_join_record.setVisibility(View.VISIBLE);
            this.tv_user_join_number.setText(MessageFormat.format("您参与了：{0}人次", String.valueOf(this.goods.getCurrent().getGonumber())));
            this.tv_user_join_codes.setText(MessageFormat.format("云购号码：{0}", this.goods.getCurrent().getGoucode()));
        } else {
            this.tv_join_message.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     */
    private void showShopCartNumber() {
        new Handler().post(() -> {
            int count = DbHelper.getTotalShopCart();
            if (count > 0) {
                if (this.badgeView == null) {
                    this.badgeView = new BadgeView(mContext, rl_join);
                    this.badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                }
                this.badgeView.setText(count + "");
                this.badgeView.setBadgeMargin(CommonHelper.dp2px(mContext, 13), CommonHelper.dp2px(mContext, 10));
                this.badgeView.show();
            }
        });
    }

    /**
     * 加载商品参与记录
     *
     * @param pageIndex
     */
    private void loadShopRecords(int pageIndex) {
        String url = MessageFormat.format("{0}/{1}/{2}/{3}", URLS.GOODS_SHOP_RECORDS, goodsId, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
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
                    if (pageIndex == 1) {
                        listRecords.clear();
                    }
                    List<ShopRecordsModel> list = JSON.parseArray(message.getData(), ShopRecordsModel.class);
                    if (list.size() > 0) {
                        listRecords.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                    if (list.size() < SysConstant.PAGE_SIZE) {
                        xRefreshView.setPullLoadEnable(false);
                    }
                } catch (Exception ex) {

                } finally {
                    if (pageIndex == 1) {
                        xRefreshView.stopRefresh();
                    }
                    if (pageIndex > 1) {
                        xRefreshView.stopLoadMore();
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });
    }

    /**
     * 去购物车
     */
    private void goShopCart() {
        Intent intent = new Intent();
        intent.setAction(SysConstant.ACTION_MYYG_RECEIVE_SHOP_CART);
        this.sendBroadcast(intent);
        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case GOODS_GO_SHOP_REQUEST_CODE:
                setResult(RESULT_OK);
                this.finish();
                break;
        }
    }
}
