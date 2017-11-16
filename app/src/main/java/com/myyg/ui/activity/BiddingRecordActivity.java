package com.myyg.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
import com.myyg.constant.SysConstant;
import com.myyg.constant.SysEnums;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.db.DbHelper;
import com.myyg.model.MessageResult;
import com.myyg.ui.fragment.BiddingRecordFragment;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.viewpagerindicator.BadgeView;
import com.myyg.widget.viewpagerindicator.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class BiddingRecordActivity extends BaseActivity {
    public static final int GOODS_GO_SHOP_REQUEST_CODE = 0x01;

    private LinePagerIndicator line_tab_indicator;

    private ViewPager view_pager;

    private List<FragmentPagerModel> listFragmentPager;

    private SampleFragmentPagerAdapter adapter;

    private BadgeView badgeView;

    private Toolbar bar_title;

    private ShopCartBroadcastReceiver mBroadcastReceiver;

    @Override
    public void initView() {
        setContentView(R.layout.activity_bidding_record);
        this.bar_title = (Toolbar) this.findViewById(R.id.bar_title);
        this.line_tab_indicator = (LinePagerIndicator) this.findViewById(R.id.line_tab_indicator);
        this.line_tab_indicator.setSelectedColor(0xFFf95667);
        this.line_tab_indicator.setIndicatorColor(0xFFf95667);
        this.line_tab_indicator.setUnderlineHeight(0);
        this.line_tab_indicator.setIndicatorHeight(10);
        this.line_tab_indicator.setBackgroundColor(0xFFffffff);
        this.view_pager = (ViewPager) this.findViewById(R.id.view_pager);
    }

    @Override
    public void initData() {
        this.listFragmentPager = new ArrayList<>();
        this.listFragmentPager.add(new FragmentPagerModel("全部", BiddingRecordFragment.newInstance(SysEnums.EnumJoinStatus.All)));
        this.listFragmentPager.add(new FragmentPagerModel("进行中", BiddingRecordFragment.newInstance(SysEnums.EnumJoinStatus.Process)));
        this.listFragmentPager.add(new FragmentPagerModel("已揭晓", BiddingRecordFragment.newInstance(SysEnums.EnumJoinStatus.Opened)));
        this.adapter = new SampleFragmentPagerAdapter(this.mContext, this.getSupportFragmentManager(), listFragmentPager);
        this.view_pager.setAdapter(adapter);
        this.line_tab_indicator.setViewPager(this.view_pager);
        this.view_pager.setOffscreenPageLimit(3);

        int px = CommonHelper.dp2px(this.mContext, 6);
        this.badgeView = new BadgeView(mContext, bar_title);
        this.badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        this.badgeView.setBadgeMargin(px, CommonHelper.dp2px(this.mContext, 8));
        this.badgeView.setBadgeBackgroundColor(R.color.color_000000);

        IntentFilter filter = new IntentFilter();
        filter.addAction(SysConstant.ACTION_MYYG_RECEIVE_SHOP_CART);
        if (this.mBroadcastReceiver == null) {
            this.mBroadcastReceiver = new ShopCartBroadcastReceiver();
        }
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void fillView() {
        this.setToolBar("云购记录");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_bidding_record, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                UIHelper.startActivity(this.mContext, SettingActivity.class);
                break;
            case R.id.action_message_center:
                UIHelper.startActivity(this.mContext, NoticeActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取未读消息数
     */
    private void getNoticeCount() {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.USER_NOTICE_COUNT, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    return;
                }
                int count = Integer.parseInt(result.getData());
                badgeView.hide();
                if (count > 0) {
                    badgeView.setText(count + "");
                    badgeView.show();
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
                this.finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getNoticeCount();
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
