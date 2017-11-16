package com.myyg.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;

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
import com.myyg.model.GoodsModel;
import com.myyg.model.MessageResult;
import com.myyg.model.WinningMessageModel;
import com.myyg.ui.view.WinningDialog;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WinningRecordActivity extends BaseActivity {
    private static final String TAG = WinningRecordActivity.class.getSimpleName();

    private XRefreshView xRefreshView;

    private RecyclerView rv_winning_record;

    private RecyclerAdapter<GoodsModel> adapter;

    private List<GoodsModel> listGoods = new ArrayList<>();

    private int pageIndex = 1;

    private String baseUrl = CommonHelper.getStaticBasePath();

    @Override
    public void initView() {
        setContentView(R.layout.activity_winning_record);
        this.rv_winning_record = (RecyclerView) findViewById(R.id.rv_winning_record);
        this.xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        this.bindRecycleView();
        this.bindListener();
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("中奖记录");
        this.getWinning();
    }

    /**
     * 绑定用户中奖纪录数据
     */
    private void bindRecycleView() {
        this.listGoods.clear();
        this.loadData();
        this.xRefreshView.setPullLoadEnable(true);
        MyygRefreshHeader header = new MyygRefreshHeader(WinningRecordActivity.this);
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setAutoLoadMore(true);
        this.adapter = new RecyclerAdapter<GoodsModel>(WinningRecordActivity.this, this.listGoods, R.layout.item_winning_record) {
            @Override
            public void convert(RecyclerViewHolder helper, GoodsModel item, int position) {
                String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, item.getThumb());
                helper.setImageUrl(R.id.image_thumb, imgUrl);
                helper.setText(R.id.tv_title, item.getTitle());
                helper.setText(R.id.tv_qishu, "期号：" + item.getQishu());
                String win_code = MessageFormat.format("<font color=\"#333333\">幸运号码：</font><font color=\"#f85767\">{0}</font>", item.getWin_code());
                helper.setText(R.id.tv_win_code, Html.fromHtml(win_code));
                helper.setText(R.id.tv_zongrenshu, "总需：" + item.getZongrenshu() + "人次");
                helper.setText(R.id.tv_canyurenshu, "本期参与：" + item.getCanyurenshu() + "人次");
                String time = DateHelper.getDefaultDate(new Date(Long.parseLong(item.getWin_time()) * 1000));
                helper.setText(R.id.tv_win_time, "揭晓时间：" + time);
                helper.setText(R.id.tv_status, item.getStatus_desc() + "");
                ImageView iv_price = helper.getView(R.id.iv_price);
                iv_price.setVisibility(View.GONE);
                if (item.getYunjiage() > 1) {
                    iv_price.setVisibility(View.VISIBLE);
                }
            }
        };
        this.rv_winning_record.setHasFixedSize(true);
        this.rv_winning_record.setLayoutManager(new LinearLayoutManager(WinningRecordActivity.this));
        this.rv_winning_record.setAdapter(this.adapter);
        this.adapter.setCustomLoadMoreView(new XRefreshViewFooter(WinningRecordActivity.this));
    }

    public void bindListener() {
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    pageIndex = 1;
                    loadData();
                }, 1000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                loadData();
            }

        });
        this.adapter.setOnItemClickListener((parent, position) ->
        {
            GoodsModel goods = this.listGoods.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt(WillConfirmActivity.GOODS_ID_CODE, goods.getId());
            bundle.putSerializable(WillConfirmActivity.GOODS_MODEL_CODE, goods);
            UIHelper.startActivity(this.mContext, WillConfirmActivity.class, bundle);
        });
    }

    private void getWinning() {
        // 获取是否存在中奖信息
        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.USER_WINNING, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    return;
                }
                List<WinningMessageModel> listWinMessage = JSON.parseArray(result.getData(), WinningMessageModel.class);
                showWinning(listWinMessage);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyLog.e(TAG, s);
            }
        });
    }

    /**
     * 显示中心信息
     */
    private void showWinning(List<WinningMessageModel> listWinMessage) {
        if (listWinMessage.size() == 0) {
            return;
        }
        WinningMessageModel winning = listWinMessage.get(0);
        WinningDialog window = new WinningDialog(this.mContext, winning.getTitle(), winning.getQishu());
        window.show();
        window.setOnDismissListener(dialog -> {
            this.settingWinningRead(winning);
            listWinMessage.remove(winning);
            showWinning(listWinMessage);
        });
    }

    /**
     * 设置中奖消息为已读
     *
     * @param model
     */
    private void settingWinningRead(WinningMessageModel model) {
        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        params.addBodyParameter("notice_id", model.getId());
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_SETTING_WINNING_READ, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {

                }
                MyLog.e(TAG, result.getMsg());
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyLog.e(TAG, s);
            }
        });
    }

    /**
     * 获取用户最新中奖纪录数据
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.USER_WIN_LIST, pageIndex, SysConstant.PAGE_SIZE);
        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(0);
        RequestParams params = BaseApplication.
                getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        xRefreshView.stopRefresh();
                        UIHelper.toastMessage(mContext, message.getMsg());
                        return;
                    }
                    List<GoodsModel> list = JSON.parseArray(message.getData(), GoodsModel.class);
                    listGoods.addAll(list);
                    adapter.notifyDataSetChanged();
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
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {

                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopLoadMore();
            }
        });
    }

}
