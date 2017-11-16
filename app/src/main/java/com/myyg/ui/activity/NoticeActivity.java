package com.myyg.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.RelativeLayout;

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
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysHtml;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.NoticeModel;
import com.myyg.utils.DateHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshFooter;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/29.
 */
public class NoticeActivity extends BaseActivity {
    private List<NoticeModel> listNotice = new ArrayList<>();

    private RecyclerAdapter<NoticeModel> adapter;

    private RecyclerView rv_notice;

    private XRefreshView xRefreshView;

    private int pageIndex = 1;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_notice);
        this.xRefreshView = (XRefreshView) this.findViewById(R.id.xRefreshView);
        this.rv_notice = (RecyclerView) this.findViewById(R.id.rv_notice);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("通知");
        this.bindRecycleView();
    }

    /**
     *
     */
    private void bindRecycleView() {
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(this.mContext);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.xRefreshView.setAutoRefresh(true);
        this.adapter = new RecyclerAdapter<NoticeModel>(this.mContext, this.listNotice, R.layout.item_notice) {
            @Override
            public void convert(RecyclerViewHolder helper, NoticeModel item, int position) {
                String title = item.getTitle();
                if (item.getIs_read() == 0) {
                    title = MessageFormat.format("<font color=\"#c62435\">[未读]</font><font color=\"#333333\">{0}</font>", title);
                } else {
                    title = MessageFormat.format("<font color=\"#888888\">{0}</font>", title);
                }
                String time = MessageFormat.format("<font color=\"{0}\">{1}</font>", item.getIs_read() == 0 ? "#333333" : "#888888", DateHelper.getDefaultDate(item.getSend_time() * 1000));
                helper.setText(R.id.tv_title, Html.fromHtml(title));
                helper.setText(R.id.tv_time, Html.fromHtml(time));
                RelativeLayout rl_content = helper.getView(R.id.rl_content);
                rl_content.setOnClickListener(v -> {
                    item.setIs_read(1);
                    adapter.notifyItemChanged(position);
                    String linkUrl = MessageFormat.format("{0}/{1}", SysHtml.HTML_NOTICE_DETAILS, item.getId());
                    Bundle bundle = new Bundle();
                    bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, linkUrl);
                    UIHelper.startActivity(mContext, WebBrowseActivity.class, bundle);
                    readNotice(item.getId());
                });
            }
        };
        this.rv_notice.setHasFixedSize(true);
        this.rv_notice.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rv_notice.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new MyygRefreshFooter(this.mContext));
    }

    @Override
    public void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                xRefreshView.setPullLoadEnable(true);
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    listNotice.clear();
                    loadData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }
        });
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.USER_NOTICE_LIST, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<NoticeModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    list = JSON.parseArray(message.getData(), NoticeModel.class);
                    listNotice.addAll(list);
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

            }
        });
    }

    /**
     * @param noticeId
     */
    private void readNotice(int noticeId) {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("id", noticeId + "");
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_READ_NOTICE, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    MyLog.i(TAG, result.getMsg());
                    return;
                }
                MyLog.i(TAG, "通知读取成功");
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyLog.i(TAG, s);
            }
        });
    }
}
