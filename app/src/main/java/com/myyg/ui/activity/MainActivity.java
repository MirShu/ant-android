package com.myyg.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.myyg.R;
import com.myyg.adapter.FragmentPagerModel;
import com.myyg.adapter.MainFragmentPagerAdapter;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysConstant;
import com.myyg.db.DbHelper;
import com.myyg.listener.OnLoginVerifyListener;
import com.myyg.model.GoodsModel;
import com.myyg.model.UserModel;
import com.myyg.ui.fragment.AnnouncedFragment;
import com.myyg.ui.fragment.BaskOrderFragment;
import com.myyg.ui.fragment.HomeFragment;
import com.myyg.ui.fragment.MeFragment;
import com.myyg.ui.fragment.ShopCartFragment;
import com.myyg.ui.view.AnimationDialog;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.widget.viewpagerindicator.FadingTabPagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnLoginVerifyListener, HomeFragment.OnHomeListener, Handler.Callback {
    private final String TAG = MainActivity.class.getSimpleName();

    public static final String MAIN_TAB_INDEX = "main_tab_index";

    private ViewPager view_pager;

    private FadingTabPagerIndicator fade_tab_indicator;

    private List<FragmentPagerModel> listFragmentPager;

    private MainFragmentPagerAdapter adapter;

    private MainBroadcastReceiver mBroadcastReceiver;

    private ShopCartFragment shopCartFragment;

    private Handler mHandler;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_main);
        this.view_pager = (ViewPager) this.findViewById(R.id.view_pager);
        this.fade_tab_indicator = (FadingTabPagerIndicator) this.findViewById(R.id.fade_tab_indicator);
    }

    @Override
    public void initData() {
        this.shopCartFragment = new ShopCartFragment(this);
        listFragmentPager = new ArrayList<>();
        listFragmentPager.add(new FragmentPagerModel("首页", new HomeFragment()));
        listFragmentPager.add(new FragmentPagerModel("最新揭晓", new AnnouncedFragment()));
        listFragmentPager.add(new FragmentPagerModel("晒单", new BaskOrderFragment()));
        listFragmentPager.add(new FragmentPagerModel("购物车", this.shopCartFragment));
        listFragmentPager.add(new FragmentPagerModel("我的云购", new MeFragment(this)));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SysConstant.ACTION_MYYG_RECEIVE_SHOP_CART);
        intentFilter.addAction(SysConstant.ACTION_MYYG_RECEIVE_HOME);
        if (this.mBroadcastReceiver == null) {
            this.mBroadcastReceiver = new MainBroadcastReceiver();
        }
        this.registerReceiver(mBroadcastReceiver, intentFilter);
        this.mHandler = new Handler(this);
    }

    @Override
    public void fillView() {
        this.adapter = new MainFragmentPagerAdapter(this.mContext, getSupportFragmentManager(), listFragmentPager);
        this.view_pager.setAdapter(this.adapter);
        this.fade_tab_indicator.setViewPager(this.view_pager);
        int count = DbHelper.getTotalShopCart();
        if (count > 0) {
            this.fade_tab_indicator.setBadge(3, count);
        }
    }

    @Override
    public void bindListener() {
        this.shopCartFragment.setListener(() -> this.fade_tab_indicator.hideBadge(3));
    }

    @Override
    public void onCallback(boolean isLogin, int tabIndex) {
        if (!isLogin) {
            Bundle bundle = new Bundle();
            bundle.putInt(MAIN_TAB_INDEX, tabIndex);
            UIHelper.startActivityForResult(this.mContext, LoginActivity.class, 0x01, bundle);
            new Handler().postDelayed(() -> {
                this.fade_tab_indicator.setCurrentItem(0);
            }, 500);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        // 登录成功
        if (requestCode == 0x01) {
            int tabIndex = data.getIntExtra(MAIN_TAB_INDEX, listFragmentPager.size() - 1);
            this.fade_tab_indicator.setCurrentItem(tabIndex);
        }
        // 退出登录
        if (requestCode == 0x02) {
            this.fade_tab_indicator.setCurrentItem(0);
        }
    }

    @Override
    public void onTabSelect(int index) {
        this.view_pager.setCurrentItem(index);
    }

    @Override
    public void onAddShopCart(GoodsModel model, int[] location, int width) {
        DbHelper.addShopCart(model);
        int height = this.fade_tab_indicator.getHeight();
        int targetX = BaseApplication.WINDOW_WIDTH / 10 * 6;
        int targetY = BaseApplication.WINDOW_HEIGHT - CommonHelper.getStatusBarHeight(this.mContext) - height * 2;
        int[] targetLocation = new int[]{targetX, targetY};
        AnimationDialog dialog = new AnimationDialog(this.mContext, model, location, width, targetLocation);
        dialog.show();
        this.showShopCartNumber(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.showShopCartNumber(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBroadcastReceiver != null) {
            this.unregisterReceiver(this.mBroadcastReceiver);
        }
    }

    /**
     * 显示我的主页
     */
    private void showHome() {
        this.view_pager.setCurrentItem(0);
    }

    /**
     * 显示购物车信息
     */
    private void showShopCart() {
        this.view_pager.setCurrentItem(3);
        this.shopCartFragment.showMyShopCart();
    }

    /**
     * 显示购物车数量
     */
    private void showShopCartNumber(long delayMillis) {
        this.mHandler.postDelayed(() -> {
            int count = DbHelper.getTotalShopCart();
            this.fade_tab_indicator.hideBadge(3);
            if (count > 0) {
                this.fade_tab_indicator.setBadge(3, count);
            }
        }, delayMillis);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    /**
     * 主页广播接收
     */
    public class MainBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case SysConstant.ACTION_MYYG_RECEIVE_HOME:
                    showHome();
                    break;
                case SysConstant.ACTION_MYYG_RECEIVE_SHOP_CART:
                    showShopCart();
                    break;
            }
        }
    }
}