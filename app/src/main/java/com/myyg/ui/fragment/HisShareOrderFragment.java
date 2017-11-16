package com.myyg.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.myyg.base.BaseApplication;
import com.myyg.common.ImageLoaderEx;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysHtml;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.GoodsModel;
import com.myyg.model.HisShareOrderModel;
import com.myyg.model.MessageResult;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.ui.activity.WebBrowseActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HisShareOrderFragment extends Fragment {
    private final static String TAG = ObtainGoodsFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";

    private String mUserId;

    private XRefreshView xRefreshView;

    private RecyclerView rv_share_order;

    private RecyclerAdapter<HisShareOrderModel> adapter;

    private List<HisShareOrderModel> listShare = new ArrayList<>();

    private int pageIndex = 1;

    private String baseUrl = CommonHelper.getStaticBasePath();

    private SimpleDateFormat df = new SimpleDateFormat("MM月dd日 MM:ss");

    /**
     * @param mUserId
     * @return
     */
    public static HisShareOrderFragment newInstance(String mUserId) {
        HisShareOrderFragment fragment = new HisShareOrderFragment();
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
        View view = inflater.inflate(R.layout.fragment_his_share_order, container, false);
        this.xRefreshView = (XRefreshView) view.findViewById(R.id.xRefreshView);
        this.rv_share_order = (RecyclerView) view.findViewById(R.id.rv_share_order);
        this.bindRecycleView();
        this.bindListener();
        return view;
    }

    /**
     *
     */
    private void bindRecycleView() {
        this.listShare.clear();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.xRefreshView.setAutoRefresh(true);
        this.adapter = new RecyclerAdapter<HisShareOrderModel>(this.getActivity(), this.listShare, R.layout.item_his_share_order) {
            @Override
            public void convert(RecyclerViewHolder helper, HisShareOrderModel item, int position) {
                helper.setText(R.id.tv_sd_title, item.getSd_title());
                helper.setText(R.id.tv_sd_content, Html.fromHtml(item.getSd_content()));
                String time = DateHelper.getCustomerDate(item.getSd_time() * 1000, df);
                helper.setText(R.id.tv_sd_time, time);
                LinearLayout ll_photo = helper.getView(R.id.ll_photo);
                ll_photo.removeAllViews();
                String photolist = item.getSd_photolist();
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
        this.rv_share_order.setHasFixedSize(true);
        this.rv_share_order.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.rv_share_order.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(this.getActivity()));
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
            String url = MessageFormat.format("{0}/{1}", SysHtml.HTML_SHAIDAN_DETAILS, listShare.get(position).getSd_id());
            bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, url);
            UIHelper.startActivity(getActivity(), WebBrowseActivity.class, bundle);
        });
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}/{3}", URLS.MEMBER_SHARE_LIST, this.mUserId, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<HisShareOrderModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    list = JSON.parseArray(message.getData(), HisShareOrderModel.class);
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
