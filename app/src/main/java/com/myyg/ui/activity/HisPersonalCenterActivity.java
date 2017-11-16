package com.myyg.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.adapter.FragmentPagerModel;
import com.myyg.adapter.SampleFragmentPagerAdapter;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.common.ImageLoaderEx;
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.UserModel;
import com.myyg.ui.fragment.CloudRecordsFragment;
import com.myyg.ui.fragment.HisShareOrderFragment;
import com.myyg.ui.fragment.ObtainGoodsFragment;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.RoundedImageView;
import com.myyg.widget.viewpagerindicator.LinePagerIndicator;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16.
 */
public class HisPersonalCenterActivity extends BaseActivity {
    private final static String TAG = HisPersonalCenterActivity.class.getSimpleName();
    public static final int GOODS_GO_SHOP_REQUEST_CODE = 0x01;
    public final static String HIS_USER_ID = "his_user_id";
    private TextView tv_level, tv_name;

    private RoundedImageView riv_avatar;

    private String baseUrl = CommonHelper.getStaticBasePath();

    private LinePagerIndicator line_tab_indicator;

    private ViewPager view_pager;

    private List<FragmentPagerModel> listFragmentPager;

    private SampleFragmentPagerAdapter adapter;

    private String mUserId;

    private ShopCartBroadcastReceiver mBroadcastReceiver;

    @Override
    public void initView() {
        setContentView(R.layout.activity_his_personal_center);
        this.line_tab_indicator = (LinePagerIndicator) this.findViewById(R.id.line_tab_indicator);
        this.line_tab_indicator.setSelectedColor(0xFFf95667);
        this.line_tab_indicator.setIndicatorColor(0xFFf95667);
        this.line_tab_indicator.setUnderlineHeight(2);
        this.line_tab_indicator.setIndicatorHeight(10);
        this.line_tab_indicator.setBackgroundColor(0xFFffffff);
        this.view_pager = (ViewPager) this.findViewById(R.id.view_pager);
        this.tv_level = (TextView) findViewById(R.id.tv_level);
        this.tv_name = (TextView) findViewById(R.id.tv_name);
        this.riv_avatar = (RoundedImageView) findViewById(R.id.riv_avatar);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.mUserId = bundle.getString(HIS_USER_ID);
        this.listFragmentPager = new ArrayList<>();
        this.listFragmentPager.add(new FragmentPagerModel("云购记录", CloudRecordsFragment.newInstance(this.mUserId)));
        this.listFragmentPager.add(new FragmentPagerModel("获得的商品", ObtainGoodsFragment.newInstance(this.mUserId)));
        this.listFragmentPager.add(new FragmentPagerModel("晒单", HisShareOrderFragment.newInstance(this.mUserId)));
        this.adapter = new SampleFragmentPagerAdapter(this.mContext, this.getSupportFragmentManager(), listFragmentPager);
        this.view_pager.setAdapter(adapter);
        this.line_tab_indicator.setViewPager(this.view_pager);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SysConstant.ACTION_MYYG_RECEIVE_SHOP_CART);
        if (this.mBroadcastReceiver == null) {
            this.mBroadcastReceiver = new ShopCartBroadcastReceiver();
        }
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void fillView() {
        this.setToolBar("个人主页");
        this.loadData();
    }

    /**
     *
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}", URLS.MEMBER_GET_INFO, this.mUserId);
        HttpUtils http = new HttpUtils();
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
                    UserModel model = JSON.parseObject(message.getData(), UserModel.class);
                    tv_level.setText(model.getLevel());
                    tv_name.setText(model.getName());
                    String avatar = MessageFormat.format("{0}/{1}", baseUrl, model.getAvatar());
                    ImageLoader.getInstance().displayImage(avatar, riv_avatar, ImageLoaderEx.getDisplayImageOptions());
                } catch (Exception ex) {

                } finally {
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case GOODS_GO_SHOP_REQUEST_CODE:
                this.goShopCart();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBroadcastReceiver != null) {
            this.unregisterReceiver(this.mBroadcastReceiver);
        }
    }

    /**
     * 去购物车
     */
    private void goShopCart() {
        setResult(RESULT_OK);
        this.finish();
    }

    /**
     * 进入购物车广播接收
     */
    public class ShopCartBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case SysConstant.ACTION_MYYG_RECEIVE_SHOP_CART:
                    goShopCart();
                    break;
            }
        }
    }
}
