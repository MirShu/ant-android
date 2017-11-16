package com.myyg.ui.fragment;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
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
import com.myyg.adapter.CountdownAdapter;
import com.myyg.adapter.recycler.RecyclerAdapter;
import com.myyg.adapter.recycler.RecyclerViewHolder;
import com.myyg.banner.CommonBanner;
import com.myyg.base.BaseApplication;
import com.myyg.common.NetworkImageViewHolder;
import com.myyg.constant.SharedKeys;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.BannerModel;
import com.myyg.model.GoodsCategoryModel;
import com.myyg.model.GoodsListModel;
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.model.UserModel;
import com.myyg.ui.activity.GoodsActivity;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.ui.activity.GoodsTypeActivity;
import com.myyg.ui.activity.LoginActivity;
import com.myyg.ui.activity.RecommendGoodsActivity;
import com.myyg.ui.activity.SearchActivity;
import com.myyg.ui.activity.SpecificCommodityActivity;
import com.myyg.ui.activity.WebBrowseActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.SharedHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private List<GoodsCategoryModel> listCategory = new ArrayList<>();

    private List<GoodsModel> listAnnounced = new ArrayList<>();

    private List<GoodsModel> listRecommend = new ArrayList<>();

    private List<GoodsModel> listGoods = new ArrayList<>();

    private CommonBanner cb_banner;

    private RecyclerAdapter<GoodsCategoryModel> categoryAdapter;

    private CountdownAdapter<GoodsModel> adapter;

    private RecyclerAdapter<GoodsModel> recommendAdapter;

    private RecyclerAdapter<GoodsModel> goodsAdapter;

    private RecyclerView rv_goods_type;

    private RecyclerView rv_announced;

    private RecyclerView rv_recommend;

    private RecyclerView rv_goods;

    private XRefreshView xRefreshView;

    private XScrollView sv_wrap;

    private int pageIndex = 1;

    private int pageSize = 4;

    private OnHomeListener listener;

    private ImageView iv_announced;

    private ImageView iv_goods;

    private ImageView iv_recommend;

    private RecyclerView.LayoutManager layoutManager;

    private String baseUrl = CommonHelper.getStaticBasePath();

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.iv_announced = (ImageView) view.findViewById(R.id.iv_announced);
        this.iv_goods = (ImageView) view.findViewById(R.id.iv_goods);
        this.iv_recommend = (ImageView) view.findViewById(R.id.iv_recommend);
        this.sv_wrap = (XScrollView) view.findViewById(R.id.sv_wrap);
        this.cb_banner = (CommonBanner) view.findViewById(R.id.cb_banner);
        this.xRefreshView = (XRefreshView) view.findViewById(R.id.xrefreshview);
        ImageView iv_search = (ImageView) view.findViewById(R.id.iv_search);
        iv_search.setOnClickListener((v) -> UIHelper.startActivity(this.getActivity(), SearchActivity.class));
        ImageView iv_user = (ImageView) view.findViewById(R.id.iv_user);
        iv_user.setOnClickListener((v) -> {
            UserModel user = BaseApplication.getUser();
            if (user == null || TextUtils.isEmpty(user.getUid())) {
                UIHelper.startActivity(this.getActivity(), LoginActivity.class);
                return;
            }
            this.listener.onTabSelect(4);
        });
        this.rv_goods_type = (RecyclerView) view.findViewById(R.id.rv_goods_type);
        this.rv_announced = (RecyclerView) view.findViewById(R.id.rv_announced);
        this.rv_recommend = (RecyclerView) view.findViewById(R.id.rv_recommend);
        this.rv_goods = (RecyclerView) view.findViewById(R.id.rv_goods);
        this.bindBanner();
        this.bindRecycleView();
        this.bindListener();
        return view;
    }

    /**
     * 绑定事件
     */
    private void bindListener() {
        this.iv_announced.setOnClickListener((view) -> this.listener.onTabSelect(1));
        this.iv_recommend.setOnClickListener((view) -> UIHelper.startActivity(this.getActivity(), RecommendGoodsActivity.class));
        this.iv_goods.setOnClickListener((view) -> UIHelper.startActivity(this.getActivity(), GoodsActivity.class));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeListener) {
            listener = (OnHomeListener) context;
        } else {
            throw new RuntimeException("请在MainActivity中实现OnHomeListener接口");
        }
    }

    /**
     *
     */
    private void bindBanner() {
        List<String> networkImages = new ArrayList<>();
        List<BannerModel> listBanner = JSON.parseArray(String.valueOf(SharedHelper.get(SharedKeys.HOME_BANNER, "")), BannerModel.class);
        if (listBanner != null) {
            for (BannerModel model : listBanner) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, model.getImg());
                networkImages.add(imgUrl);
            }
        }
        this.cb_banner.setPages(() -> new NetworkImageViewHolder(), networkImages).setPageIndicator(new int[]{R.mipmap.ic_banner_default, R.mipmap.ic_banner_checked});
        this.cb_banner.getViewPager().setOnItemClickListener((position) -> {
            BannerModel banner = listBanner.get(position);
            Bundle bundle = new Bundle();
            bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, banner.getLink());
            bundle.putString(WebBrowseActivity.WEB_BROWSE_TITLE_TAG, banner.getTitle());
            UIHelper.startActivity(getActivity(), WebBrowseActivity.class, bundle);
        });
        this.cb_banner.startTurning(3000);
    }

    /**
     * RecycleView绑定数据
     */

    private void bindRecycleView() {
        this.listGoods.clear();
        this.categoryAdapter = new RecyclerAdapter<GoodsCategoryModel>(this.getActivity(), listCategory, R.layout.item_home_goods_type) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsCategoryModel item, int position) {
                if (TextUtils.isEmpty(item.getCateid())) {
                    helper.setImageResource(R.id.iv_goods_type, R.mipmap.ic_home_type);
                } else {
                    String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getUrl());

                }
                helper.setText(R.id.tv_goods_type_name, item.getName());
            }
        };
        this.adapter = new CountdownAdapter<GoodsModel>(this.getActivity(), listAnnounced, R.layout.item_announced_home) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.iv_goods_img, imgUrl);
                helper.setText(R.id.tv_goods_name, item.getTitle());
                helper.setText(R.id.tv_qishu, MessageFormat.format("期号：{0}", item.getQishu()));
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
                if (item.getYunjiage() > 1) {
                    iv_price.setVisibility(View.VISIBLE);
                }
                LinearLayout ll_announced = helper.getView(R.id.ll_announced);
                ll_announced.setVisibility(View.GONE);
                LinearLayout ll_winning_user = helper.getView(R.id.ll_winning_user);
                ll_winning_user.setVisibility(View.GONE);
                if (item.getQ_showtime().equals("Y") && item.getCountdown() > 0) {
                    ll_announced.setVisibility(View.VISIBLE);
                    return;
                }
                ll_winning_user.setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_q_user, Html.fromHtml(MessageFormat.format("获得用户：<font color=\"#0171bb\">{0}</font>", item.getQ_user())));
                helper.setText(R.id.tv_q_user_code, Html.fromHtml(MessageFormat.format("<font color=\"#c62334\">幸运号码：{0}</font>", item.getQ_user_code())));
            }
        };
        this.recommendAdapter = new RecyclerAdapter<GoodsModel>(this.getActivity(), listRecommend, R.layout.item_recommend) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.iv_goods_img, imgUrl);
                helper.setText(R.id.tv_goods_name, item.getTitle());
                helper.setText(R.id.tv_zongrenshu, MessageFormat.format("总需：{0}人次", String.valueOf(item.getZongrenshu())));
                RoundCornerProgressBar rc_bar = helper.getView(R.id.rc_bar);
                rc_bar.setMax(100);
                rc_bar.setProgress(item.getCanyurenshu() * 100 / item.getZongrenshu());
                helper.setText(R.id.tv_canyurenshu, item.getCanyurenshu() + "");
                helper.setText(R.id.tv_shenyurenshu, item.getShenyurenshu() + "");
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
                if (item.getYunjiage() > 1) {
                    iv_price.setVisibility(View.VISIBLE);
                }
            }
        };
        this.goodsAdapter = new RecyclerAdapter<GoodsModel>(this.getActivity(), this.listGoods, R.layout.item_goods) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                ImageView iv_goods_img = helper.getView(R.id.iv_goods_img);
                helper.setImageUrl(R.id.iv_goods_img, imgUrl);
                helper.setText(R.id.tv_goods_name, item.getTitle());
                RoundCornerProgressBar rc_schedule = helper.getView(R.id.rc_schedule);
                rc_schedule.setMax(100);
                rc_schedule.setProgress(item.getCanyurenshu() * 100 / item.getZongrenshu());
                helper.setText(R.id.tv_schedule, rc_schedule.getProgress() + "%");
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
                if (item.getYunjiage() > 1) {
                    iv_price.setVisibility(View.VISIBLE);
                }


                /**
                 *加入购物车
                 */
                ImageView iv_goods_add = helper.getView(R.id.iv_goods_add);
                iv_goods_add.setOnClickListener(v -> {
                    if (listener != null) {
                        int[] location = new int[2];
                        iv_goods_img.getLocationOnScreen(location);
                        int width = iv_goods_img.getWidth();
                        listener.onAddShopCart(item, location, width);
                    }
                });
            }
        };
        this.rv_goods_type.setHasFixedSize(true);
        this.rv_goods_type.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        this.rv_goods_type.setAdapter(categoryAdapter);

        this.rv_announced.setHasFixedSize(true);
        this.rv_announced.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        this.rv_announced.setAdapter(adapter);

        this.rv_recommend.setHasFixedSize(true);
        this.rv_recommend.setLayoutManager(new GridLayoutManager(getActivity(), 1, LinearLayoutManager.HORIZONTAL, false));
        this.rv_recommend.setAdapter(this.recommendAdapter);

        this.layoutManager = new GridLayoutManager(getActivity(), 2);
        this.rv_goods.setHasFixedSize(true);
        this.rv_goods.setLayoutManager(this.layoutManager);
        this.rv_goods.setAdapter(this.goodsAdapter);
        this.sv_wrap.smoothScrollTo(0, 0);

        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);

        this.xRefreshView.setPullLoadEnable(true);
        //this.xRefreshView.setPinnedTime(1000);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                xRefreshView.setLoadComplete(false);
                pageIndex = 1;
                listGoods.clear();
                loadAnnouncedGoods();
                loadRecommendGoods();
                loadGoods();
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadGoods();
            }
        });
        this.categoryAdapter.setOnItemClickListener((view, position) -> {
            GoodsCategoryModel category = this.listCategory.get(position);
            String typeId = category.getCateid();
            if (TextUtils.isEmpty(typeId)) {
                UIHelper.startActivity(this.getActivity(), GoodsTypeActivity.class);
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable(SpecificCommodityActivity.GOODS_CATEGORY_TAG, category);
                UIHelper.startActivity(this.getActivity(), SpecificCommodityActivity.class, bundle);
            }
        });
        this.adapter.setOnItemClickListener((parent, position) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, listAnnounced.get(position).getId());
            UIHelper.startActivity(getActivity(), GoodsDetailsActivity.class, bundle);
        });
        this.recommendAdapter.setOnItemClickListener((parent, position) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, listRecommend.get(position).getId());
            UIHelper.startActivity(getActivity(), GoodsDetailsActivity.class, bundle);
        });
        this.goodsAdapter.setOnItemClickListener((parent, position) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, listGoods.get(position).getId());
            UIHelper.startActivity(getActivity(), GoodsDetailsActivity.class, bundle);
        });
        this.loadGoodsCategory();
        this.loadAnnouncedGoods();
        this.loadRecommendGoods();
        this.loadGoods();
    }

    /**
     * 获取商品分类
     */
    private void loadGoodsCategory() {
        try {
            this.listCategory.clear();
            List<GoodsCategoryModel> list = JSON.parseArray(String.valueOf(SharedHelper.get(SharedKeys.HOME_GOODS_CATEGORY, "")), GoodsCategoryModel.class);
            this.listCategory.add(new GoodsCategoryModel("", "分类"));
            this.listCategory.addAll(list);
        } catch (Exception ex) {
            MyLog.e(TAG, ex.getMessage());
        }
    }

    /**
     * 加载最新揭晓商品
     */
    private void loadAnnouncedGoods() {
        this.listAnnounced.clear();
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.GOODS_LATEST_LIST, 1, 4);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.configDefaultHttpCacheExpiry(0);
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    List<GoodsModel> list = JSON.parseArray(message.getData(), GoodsModel.class);
                    long currentTimeMillis = System.currentTimeMillis();
                    for (GoodsModel item : list) {
                        if (item.getQ_showtime().equals("Y") && item.getCountdown() > 0) {
                            item.setEndTime(currentTimeMillis + item.getCountdown() * 1000);
                        }
                    }
                    listAnnounced.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });
    }

    /**
     * 推荐商品列表
     */
    private void loadRecommendGoods() {
        this.listRecommend.clear();
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.configDefaultHttpCacheExpiry(0);
        http.send(HttpRequest.HttpMethod.GET, URLS.GOODS_RECOMMEND_LIST, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    GoodsListModel model = JSON.parseObject(message.getData(), GoodsListModel.class);
                    listRecommend.addAll(model.getList());
                    recommendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.adapter != null) {
            this.adapter.startRefreshTime();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.adapter != null) {
            this.adapter.cancelRefreshTime();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != adapter) {
            this.adapter.cancelRefreshTime();
        }
    }

    private void loadGoods() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.GOODS_SHOP_LIST, pageIndex, pageSize);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                List<GoodsModel> list = new ArrayList<>();
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    GoodsListModel model = JSON.parseObject(message.getData(), GoodsListModel.class);
                    list = model.getList();
                    listGoods.addAll(list);
                    goodsAdapter.notifyDataSetChanged();
                }
                if (pageIndex == 1) {
                    new Handler().postDelayed(() -> xRefreshView.stopRefresh(), 1000);
                }
                if (list.size() < pageSize) {
                    xRefreshView.setLoadComplete(true);
                } else {
                    xRefreshView.stopLoadMore();
                }
                if (list.size() == pageSize) {
                    pageIndex++;
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });


    }

    public interface OnHomeListener {
        /**
         * 设置导航菜单选中
         *
         * @param index
         */
        void onTabSelect(int index);

        /**
         * 添加到购物车
         *
         * @param item     商品信息
         * @param location 商品所在屏幕位置
         */
        void onAddShopCart(GoodsModel item, int[] location, int width);
    }
}
