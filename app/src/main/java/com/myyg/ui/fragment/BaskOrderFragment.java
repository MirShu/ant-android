package com.myyg.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.myyg.adapter.CommonAdapter;
import com.myyg.adapter.ViewHolder;
import com.myyg.adapter.recycler.RecyclerAdapter;
import com.myyg.adapter.recycler.RecyclerViewHolder;
import com.myyg.base.BaseApplication;
import com.myyg.common.ImageLoaderEx;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysHtml;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.model.ShareModel;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.ui.activity.GoodsTypeActivity;
import com.myyg.ui.activity.WebBrowseActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.RoundedImageView;
import com.myyg.widget.xrefreshview.CustomGifHeader;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BaskOrderFragment extends Fragment {

    private XRefreshView xRefreshView;

    private RecyclerAdapter<ShareModel> adapter;

    private RecyclerView rv_baskorder;

    private List<ShareModel> listShare = new ArrayList<>();

    private int pageIndex = 1;

    private SimpleDateFormat format_date = new SimpleDateFormat("MM-dd HH:mm");

    private String baseUrl = CommonHelper.getStaticBasePath();

    /**
     *
     */
    public BaskOrderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baskorder, container, false);
        TextView tv_head_title = (TextView) view.findViewById(R.id.tv_head_title);
        tv_head_title.setText("晒单分享");
        this.xRefreshView = (XRefreshView) view.findViewById(R.id.xrefreshview);
        this.rv_baskorder = (RecyclerView) view.findViewById(R.id.rv_baskorder);
        ImageView iv_more = (ImageView) view.findViewById(R.id.iv_more);
        iv_more.setOnClickListener((v) -> {
            UIHelper.startActivity(this.getActivity(), GoodsTypeActivity.class);
        });
        this.listShare.clear();
        this.bindRecycleView();
        return view;
    }

    /**
     * 绑定数据
     */
    private void bindRecycleView() {
        this.adapter = new RecyclerAdapter<ShareModel>(this.getActivity(), this.listShare, R.layout.item_baskorder) {
            @Override
            public void convert(RecyclerViewHolder helper, ShareModel item, int position) {
                String avatar = MessageFormat.format("{0}/{1}", baseUrl, item.getAvatar());
                RoundedImageView riv_user_photo = helper.getView(R.id.riv_user_photo);
                ImageLoader.getInstance().displayImage(avatar, riv_user_photo, ImageLoaderEx.getDefaultDisplayImageOptions());
                String userName = item.getUsername();
                userName = TextUtils.isEmpty(userName) ? item.getMobile() : userName;
                helper.setText(R.id.tv_user_name, userName);
                String time = DateHelper.getCustomerDate(new Date(Long.parseLong(item.getTime()) * 1000L), format_date);
                helper.setText(R.id.tv_time, time);
                helper.setText(R.id.tv_title, Html.fromHtml(item.getSd_title()));
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
                        if (TextUtils.isEmpty(photo)) {
                            continue;
                        }
                        String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, photo);
                        ImageView iv = new ImageView(getActivity());
                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                        ImageLoader.getInstance().displayImage(imgUrl, iv, ImageLoaderEx.getDisplayImageOptions());
                        ll_photo.addView(iv);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
                        layoutParams.height = CommonHelper.dp2px(getActivity(), 55);
                        layoutParams.width = CommonHelper.dp2px(getActivity(), 55);
                        if (i == 1) {
                            layoutParams.leftMargin = CommonHelper.dp2px(getActivity(), 15);
                            layoutParams.rightMargin = CommonHelper.dp2px(getActivity(), 15);
                        }
                        iv.setLayoutParams(layoutParams);
                    }
                }
            }
        };
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setPullLoadEnable(true);
        this.xRefreshView.setMoveForHorizontal(false);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));
        this.rv_baskorder.setHasFixedSize(true);
        this.rv_baskorder.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.rv_baskorder.setAdapter(this.adapter);
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
            UIHelper.startActivity(getActivity(), WebBrowseActivity.class, bundle);
        });
        this.loadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible()) {
            //UIHelper.showLoading(getActivity());
            //loadData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.EXTRA_SHARE_LIST, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                UIHelper.hideLoading();
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
                UIHelper.hideLoading();
                xRefreshView.stopLoadMore();
            }
        });
    }
}
