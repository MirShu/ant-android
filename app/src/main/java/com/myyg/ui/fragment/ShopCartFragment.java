package com.myyg.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.db.DbHelper;
import com.myyg.listener.OnLoginVerifyListener;
import com.myyg.model.CartItemModel;
import com.myyg.model.MessageResult;
import com.myyg.model.ShopCartModel;
import com.myyg.model.UserModel;
import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.ui.activity.GoodsTypeActivity;
import com.myyg.ui.activity.SettlementActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.widget.NumberView;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.rey.material.widget.Button;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("ValidFragment")
public class ShopCartFragment extends Fragment {
    private final static String TAG = ShopCartFragment.class.getSimpleName();

    private List<ShopCartModel> listData = new ArrayList<>();

    private RecyclerView rv_shopcart;

    private RecyclerAdapter<ShopCartModel> shopCartAdapter;

    private XRefreshView xRefreshView;

    private Handler mHandler;

    private TextView tv_total;

    private Button btn_balance;

    private RelativeLayout rl_empty_shopcart;

    private LinearLayout ll_bottom;

    private String baseUrl = CommonHelper.getStaticBasePath();

    private View rootView;

    private OnShopCartListener listener;

    private OnLoginVerifyListener loginVerifyListener;

    public ShopCartFragment() {

    }

    public ShopCartFragment(OnLoginVerifyListener loginVerifyListener) {
        this.mHandler = new Handler();
        this.loginVerifyListener = loginVerifyListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int totalShopCart = this.getShopCartCount();
        rootView = inflater.inflate(R.layout.fragment_shopcart, container, false);
        TextView tv_head_title = (TextView) rootView.findViewById(R.id.tv_head_title);
        tv_head_title.setText("购物车");
        this.xRefreshView = (XRefreshView) rootView.findViewById(R.id.xRefreshView);
        this.ll_bottom = (LinearLayout) rootView.findViewById(R.id.ll_bottom);
        this.rl_empty_shopcart = (RelativeLayout) rootView.findViewById(R.id.rl_empty_shopcart);
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.showMyShopCart();
        if (totalShopCart <= 0) {
            this.showEmptyShopCart();
        }
        ImageView iv_more = (ImageView) rootView.findViewById(R.id.iv_more);
        iv_more.setOnClickListener((v) -> UIHelper.startActivity(this.getActivity(), GoodsTypeActivity.class));
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    loadData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                new Handler().postDelayed(() -> {
                    xRefreshView.stopLoadMore();
                    //xRefreshView.setLoadComplete(true);
                }, 1000);
            }
        });
        return rootView;
    }

    /**
     * 我的购物车
     *
     * @return
     */
    public void showMyShopCart() {
        this.xRefreshView.setVisibility(View.VISIBLE);
        this.ll_bottom.setVisibility(View.VISIBLE);
        this.rl_empty_shopcart.setVisibility(View.GONE);
        this.rv_shopcart = (RecyclerView) rootView.findViewById(R.id.rv_shopcart);
        this.tv_total = (TextView) rootView.findViewById(R.id.tv_total);
        this.btn_balance = (Button) rootView.findViewById(R.id.btn_balance);
        this.bindShopCartRecycleView();
    }

    /**
     * 空购物车
     *
     * @return
     */
    public void showEmptyShopCart() {
        this.xRefreshView.setVisibility(View.GONE);
        this.ll_bottom.setVisibility(View.GONE);
        this.rl_empty_shopcart.setVisibility(View.VISIBLE);
        Button btn_shop = (Button) rl_empty_shopcart.findViewById(R.id.btn_shop);
        btn_shop.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(SysConstant.ACTION_MYYG_RECEIVE_HOME);
            this.getActivity().sendBroadcast(intent);
        });
    }

    /**
     * @param listener
     */
    public void setListener(OnShopCartListener listener) {
        this.listener = listener;
    }

    /**
     *
     */
    private void bindShopCartRecycleView() {
        this.shopCartAdapter = new RecyclerAdapter<ShopCartModel>(getActivity(), this.listData, R.layout.item_shopcart) {
            @Override
            public void convert(RecyclerViewHolder helper, ShopCartModel item, int position) {
                String message = MessageFormat.format("总需：{0}人次，剩余<font color=\"#0171bb\">{1}</font>人次", String.valueOf(item.getTotalNumber()), String.valueOf(item.getSurplusNumber()));
                helper.setText(R.id.tv_message, Html.fromHtml(message));
                helper.setText(R.id.tv_goods_name, item.getGoodsName());
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getGoodsImage());
                helper.setImageUrl(R.id.iv_goods_img, imgUrl);
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
                if (item.getPrice() > 1) {
                    iv_price.setVisibility(View.VISIBLE);
                }
                NumberView nv_shopcart = helper.getView(R.id.nv_shopcart);
                nv_shopcart.setNumber(item.getJoinNumber());
                nv_shopcart.setMaxNumber(item.getSurplusNumber());
                nv_shopcart.setNumberListener(number -> mHandler.post(() -> {
                    DbHelper.editShopCart(item.getGoodsId(), number);
                    float totalMoney = DbHelper.getTotalMoney();
                    String strTotal = MessageFormat.format("共{0}件商品，总计：{1}元", listData.size(), totalMoney);
                    tv_total.setText(strTotal);
                }));
                ImageView iv_delete = helper.getView(R.id.iv_delete);
                iv_delete.setOnClickListener(v -> mHandler.post(() -> {
                    boolean result = DbHelper.deleteShopCart(item.getShopId());
                    if (!result) {
                        UIHelper.toastMessage(getActivity(), "购物车删除失败");
                        return;
                    }
                    loadData();
                }));
            }
        };
        this.rv_shopcart.setHasFixedSize(true);
        this.rv_shopcart.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.rv_shopcart.setAdapter(this.shopCartAdapter);
        this.xRefreshView.setPullLoadEnable(false);
        this.xRefreshView.setMoveForHorizontal(false);
        //this.xRefreshView.setPullRefreshEnable(false);
        //this.shopCartAdapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));
        this.shopCartAdapter.setOnItemClickListener((parent, position) ->
        {
            ShopCartModel model = listData.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, Integer.parseInt(model.getGoodsId()));
            UIHelper.startActivity(getActivity(), GoodsDetailsActivity.class, bundle);
        });
        this.loadData();
        this.btn_balance.setOnClickListener(v -> this.submitShopCart());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible()) {
            //UIHelper.showLoading(getActivity());
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * @return
     */
    private int getShopCartCount() {
        int totalShopCart = DbHelper.getTotalShopCart();
        return totalShopCart;
    }

    /**
     *
     */
    private void loadData() {
        try {
            listData.clear();
            List<ShopCartModel> list = DbHelper.getShopCart();
            if (list != null && list.size() > 0) {
                listData.addAll(list);
            }
            this.shopCartAdapter.notifyDataSetChanged();
            float totalMoney = DbHelper.getTotalMoney();
            String strTotal = MessageFormat.format("共{0}件商品，总计：{1}元", listData.size(), totalMoney);
            this.tv_total.setText(strTotal);
            if (this.listData.size() > 0) {
                this.xRefreshView.setVisibility(View.VISIBLE);
                this.rl_empty_shopcart.setVisibility(View.GONE);
                this.getServiceShopCart();
            } else {
                xRefreshView.stopRefresh();
                this.showEmptyShopCart();
                if (this.listener != null) {
                    this.listener.clearShopCart();
                }
            }
        } catch (Exception ex) {
            xRefreshView.stopRefresh();
            MyLog.e(TAG, ex.getMessage());
        } finally {
            UIHelper.hideLoading();
        }
    }

    /**
     * 获取购物车中对应商品信息
     */
    private void getServiceShopCart() {
        StringBuilder sb = new StringBuilder();
        for (ShopCartModel item : listData) {
            sb.append(item.getGoodsId());
            sb.append(",");
        }
        String goodsIds = sb.toString();
        if (goodsIds.length() > 0) {
            goodsIds = goodsIds.substring(0, goodsIds.length() - 1);
        }
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("item_id", goodsIds);
        http.send(HttpRequest.HttpMethod.POST, URLS.GOODS_CART_ITEM, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult result = MessageResult.parse(responseInfo.result);
                    if (result.getCode() != SysStatusCode.SUCCESS) {
                        //MyLog.e(TAG, result.getMsg());
                        return;
                    }
                    List<CartItemModel> listCartItem = JSON.parseArray(result.getData(), CartItemModel.class);
                    HashMap<String, CartItemModel> hashMap = new HashMap<>();
                    for (CartItemModel item : listCartItem) {
                        hashMap.put(item.getId(), item);
                    }
                    for (ShopCartModel item : listData) {
                        if (!hashMap.containsKey(item.getGoodsId())) {
                            continue;
                        }
                        CartItemModel model = hashMap.get(item.getGoodsId());
                        item.setTotalMoney(model.getZongrenshu());
                        item.setSurplusNumber(model.getShenyurenshu());
                        item.setPrice(model.getYunjiage());
                        item.setTotalMoney(item.getJoinNumber() * item.getPrice());
                        DbHelper.updateShopCart(item);
                    }
                    listData.clear();
                    List<ShopCartModel> list = DbHelper.getShopCart();
                    if (list != null && list.size() > 0) {
                        listData.addAll(list);
                    }
                    shopCartAdapter.notifyDataSetChanged();
                    float totalMoney = DbHelper.getTotalMoney();
                    String strTotal = MessageFormat.format("共{0}件商品，总计：{1}元", listData.size(), totalMoney);
                    tv_total.setText(strTotal);
                } catch (Exception ex) {
                    MyLog.i(TAG, ex.getMessage());
                } finally {
                    xRefreshView.stopRefresh();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopRefresh();
                MyLog.e(TAG, s);
            }
        });
    }

    /**
     * 提交购物车商品信息
     */
    private void submitShopCart() {
        if (this.listData.size() == 0) {
            return;
        }
        UserModel user = BaseApplication.getUser();
        if (user == null) {
            this.loginVerifyListener.onCallback(false, 3);
            return;
        }
        this.listData = DbHelper.getShopCart();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SettlementActivity.SHOPCART_CODE, (Serializable) listData);
        UIHelper.startActivityForResult(getActivity(), SettlementActivity.class, 0x99, bundle);
    }

    public interface OnShopCartListener {
        void clearShopCart();
    }
}
