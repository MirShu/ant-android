package com.myyg.ui.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.myyg.common.ImageLoaderEx;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysHtml;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.ShareModel;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.CustomGifHeader;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoodsShareOrderActivity extends BaseActivity {
    public static final String GOODS_ID_TAG = "goods_id_tag";

    private int goodsId;

    private XRefreshView xRefreshView;

    private RecyclerAdapter<ShareModel> adapter;

    private RecyclerView rv_share_order;

    private List<ShareModel> listShare = new ArrayList<>();

    private int pageIndex = 1;

    private SimpleDateFormat format_date = new SimpleDateFormat("MM-dd HH:mm");

    private String baseUrl = CommonHelper.getStaticBasePath();

    @Override
    public void initView() {
        setContentView(R.layout.activity_goods_share_order);
        this.xRefreshView = (XRefreshView) this.findViewById(R.id.xrefreshview);
        this.rv_share_order = (RecyclerView) this.findViewById(R.id.rv_share_order);
    }

    @Override
    public void initData() {
        this.goodsId = this.getIntent().getIntExtra(GOODS_ID_TAG, 0);
    }

    @Override
    public void fillView() {
        this.setToolBar("晒单分享");
        this.bindRecycleView();
        this.loadData();
    }

    /**
     * 绑定数据
     */
    private void bindRecycleView() {
        this.adapter = new RecyclerAdapter<ShareModel>(this.mContext, this.listShare, R.layout.item_baskorder) {
            @Override
            public void convert(RecyclerViewHolder helper, ShareModel item, int position) {
                String avatar = MessageFormat.format("{0}/{1}", baseUrl, item.getAvatar());
                helper.setImageUrl(R.id.riv_user_photo, avatar);
                String userName = item.getUsername();
                userName = TextUtils.isEmpty(userName) ? item.getMobile() : userName;
                helper.setText(R.id.tv_user_name, userName);
                String time = DateHelper.getCustomerDate(new Date(Long.parseLong(item.getTime()) * 1000L), format_date);
                helper.setText(R.id.tv_time, time);
                helper.setText(R.id.tv_title, item.getSd_title());
                helper.setText(R.id.tv_goods_name, item.getTitle());
                helper.setText(R.id.tv_qishu, MessageFormat.format("期号：{0}", item.getQishu()));
                helper.setText(R.id.tv_content, Html.fromHtml(item.getContent()));
                LinearLayout ll_photo = helper.getView(R.id.ll_photo);
                ll_photo.removeAllViews();
                String photolist = item.getPhotolist();
                if (!TextUtils.isEmpty(photolist)) {
                    String[] listPhoto = photolist.split(";");
                    // 动态构造图片并显示（最多显示3张）
                    for (int i = 0; i < 3; i++) {
                        if (i >= listPhoto.length) {
                            continue;
                        }
                        String photo = listPhoto[i];
                        String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, photo);
                        ImageView iv = new ImageView(mContext);
                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                        ImageLoader.getInstance().displayImage(imgUrl, iv, ImageLoaderEx.getDisplayImageOptions());
                        ll_photo.addView(iv);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
                        layoutParams.height = CommonHelper.dp2px(mContext, 55);
                        layoutParams.width = CommonHelper.dp2px(mContext, 55);
                        if (i == 1) {
                            layoutParams.leftMargin = CommonHelper.dp2px(mContext, 15);
                            layoutParams.rightMargin = CommonHelper.dp2px(mContext, 15);
                        }
                        iv.setLayoutParams(layoutParams);
                    }
                }
            }
        };
        MyygRefreshHeader header = new MyygRefreshHeader(this.mContext);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setPullLoadEnable(true);
        this.xRefreshView.setMoveForHorizontal(false);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(this.mContext));
        this.rv_share_order.setHasFixedSize(true);
        this.rv_share_order.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rv_share_order.setAdapter(this.adapter);
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    listShare.clear();
                    loadData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }
        });
        this.adapter.setOnItemClickListener((view, position) -> {
            Bundle bundle = new Bundle();
            String url = MessageFormat.format("{0}/{1}", SysHtml.HTML_SHAIDAN_DETAILS, listShare.get(position).getId());
            bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, url);
            UIHelper.startActivity(this.mContext, WebBrowseActivity.class, bundle);
        });
    }

    /**
     * 加载数据
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}/{3}", URLS.GOODS_SHARE_LIST, this.goodsId, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (pageIndex == 1) {
                    xRefreshView.stopRefresh();
                }
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<ShareModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    list = JSON.parseArray(message.getData(), ShareModel.class);
                    listShare.addAll(list);
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
