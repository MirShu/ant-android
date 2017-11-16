package com.myyg.ui.activity;

import android.os.Handler;
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
import com.myyg.model.MessageResult;
import com.myyg.model.PresenRecordModel;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/31.
 */
public class PresentRecordActivity extends BaseActivity {
    private int pageIndex = 1;

    private RecyclerAdapter<PresenRecordModel> adapter;

    private List<PresenRecordModel> listPresent = new ArrayList<>();
    private XRefreshView xRefreshView;

    private RecyclerView rv_present_record;

    private TextView tv_present_time;

    private TextView tv_tv_present_money;


    @Override
    public void initView() {
        setContentView(R.layout.activity_present_record);
        this.rv_present_record = (RecyclerView) findViewById(R.id.rv_present_record);
        this.xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        this.tv_present_time = (TextView) findViewById(R.id.tv_present_time);
        this.tv_tv_present_money = (TextView) findViewById(R.id.tv_tv_present_money);
        this.bindRecycleView();
        this.bindListener();
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        setToolBar("提现记录");

    }


    /**
     * 绑定用户提现记录数据
     */
    private void bindRecycleView() {
        this.listPresent.clear();
        this.loadData();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(PresentRecordActivity.this);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.adapter = new RecyclerAdapter<PresenRecordModel>(PresentRecordActivity.this, this.listPresent, R.layout.item_present_record) {
            @Override
            public void convert(RecyclerViewHolder helper, PresenRecordModel item, int position) {
                helper.setText(R.id.tv_present_time, MessageFormat.format("{0}", DateHelper.getYYYYMMDD(new Date(item.getTime() * 1000))));
                helper.setText(R.id.tv_tv_present_money, item.getMoney());

            }
        };
        this.rv_present_record.setHasFixedSize(true);
        this.rv_present_record.setLayoutManager(new LinearLayoutManager(PresentRecordActivity.this));
        this.rv_present_record.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(PresentRecordActivity.this));
    }

    public void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            public void onRefresh() {
                new Handler().postDelayed(() -> xRefreshView.stopRefresh(), 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }

        });
        this.adapter.setOnItemClickListener((parent, position) -> UIHelper.startActivity(PresentRecordActivity.this, WillConfirmActivity.class));
    }

    /**
     * 获取用户提现记录数据
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.USER_CASH_LOG, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<PresenRecordModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    list = JSON.parseArray(message.getData(), PresenRecordModel.class);
                    listPresent.addAll(list);
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
