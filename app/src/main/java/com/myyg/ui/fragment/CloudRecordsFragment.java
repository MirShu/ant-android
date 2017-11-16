package com.myyg.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.andview.refreshview.XRefreshView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.adapter.CloudRecordsAdapter;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.ui.activity.HisPersonalCenterActivity;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshFooter;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class CloudRecordsFragment extends Fragment {
    private final static String TAG = CloudRecordsFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";

    private XRefreshView xRefreshView;

    private RecyclerView rv_cloud_records;

    private CloudRecordsAdapter adapter;

    private List<GoodsModel> listGoods = new ArrayList<>();

    private int pageIndex = 1;

    private String mUserId;

    public CloudRecordsFragment() {
    }

    /**
     * @return
     */
    public static CloudRecordsFragment newInstance(String mUserId) {
        CloudRecordsFragment fragment = new CloudRecordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, mUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mUserId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_records, container, false);
        this.xRefreshView = (XRefreshView) view.findViewById(R.id.xRefreshView);
        this.rv_cloud_records = (RecyclerView) view.findViewById(R.id.rv_cloud_records);
        this.bindRecycleView();
        this.bindListener();
        return view;
    }

    /**
     *
     */
    private void bindRecycleView() {
        this.listGoods.clear();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.xRefreshView.setAutoRefresh(true);
        this.adapter = new CloudRecordsAdapter(this.getActivity(), this.rv_cloud_records, this.listGoods);
        this.rv_cloud_records.setHasFixedSize(true);
        this.rv_cloud_records.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.rv_cloud_records.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new MyygRefreshFooter(this.getActivity()));
    }

    /**
     *
     */
    private void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                xRefreshView.setPullLoadEnable(true);
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    listGoods.clear();
                    loadData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }
        });
        this.adapter.setOnItemClickListener((parent, position) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, listGoods.get(position).getId());
            UIHelper.startActivityForResult(getActivity(), GoodsDetailsActivity.class, HisPersonalCenterActivity.GOODS_GO_SHOP_REQUEST_CODE, bundle);
        });
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}/{3}", URLS.MEMBER_JOIN_LIST, this.mUserId, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<GoodsModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    list = JSON.parseArray(message.getData(), GoodsModel.class);
                    listGoods.addAll(list);
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
