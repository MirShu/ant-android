package com.myyg.ui.activity;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.AccountModel;
import com.myyg.model.MessageResult;
import com.myyg.ui.fragment.ConsumerFragment;
import com.myyg.ui.fragment.RechargeFragment;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class AccountDetailsActivity extends BaseActivity {
    private TextView tv_consumer, tv_recharge;

    private TextView tv_total_money, tv_total_consumer, tv_total_recharge;

    private View view_consumer, view_recharge;

    private DecimalFormat df = new DecimalFormat("0.00");

    private TabHost tabHost;
    private TabWidget tabWidget;
    private LocalActivityManager localActivityManager;
    private int currentTabIndex = 0;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_account_details);
        this.tabHost = (TabHost) this.findViewById(R.id.tabhost);
        this.tabWidget = (TabWidget) this.findViewById(android.R.id.tabs);
        this.tv_consumer = (TextView) this.findViewById(R.id.tv_consumer);
        this.tv_recharge = (TextView) this.findViewById(R.id.tv_recharge);

        this.tv_total_money = (TextView) this.findViewById(R.id.tv_total_money);
        this.tv_total_consumer = (TextView) this.findViewById(R.id.tv_total_consumer);
        this.tv_total_recharge = (TextView) this.findViewById(R.id.tv_total_recharge);
        this.view_consumer = this.findViewById(R.id.view_consumer);
        this.view_recharge = this.findViewById(R.id.view_recharge);
    }

    @Override
    public void initData() {
        this.localActivityManager = new LocalActivityManager(this.mContext, true);
        this.localActivityManager.dispatchResume();
        this.tabHost.setup(this.localActivityManager);
        Intent tabConsumer = new Intent(this.mContext, ConsumerActivity.class);
        this.tabHost.addTab(this.buildTabSpec("consumer", "消费明细", tabConsumer));

        Intent tabRecharge = new Intent(this.mContext, RechargeActivity.class);
        this.tabHost.addTab(this.buildTabSpec("recharge", "充值明细", tabRecharge));

        this.tabHost.setCurrentTab(this.currentTabIndex);
    }

    @Override
    public void fillView() {
        this.setToolBar("账户明细");
        this.loadData();
    }

    /**
     * 公用初始化Tab
     */
    private TabHost.TabSpec buildTabSpec(String tag, String label, final Intent content) {
        return tabHost.newTabSpec(tag).setIndicator(label).setContent(content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_consumer:
                this.consumerTab();
                break;
            case R.id.ll_recharge:
                this.rechargeTab();
                break;
        }
    }

    /**
     *
     */
    private void consumerTab() {
        this.tv_consumer.setTextColor(this.getResources().getColor(R.color.colorAccent));
        this.tv_recharge.setTextColor(this.getResources().getColor(R.color.color_999999));
        ViewGroup.LayoutParams layoutParams = view_consumer.getLayoutParams();
        layoutParams.height = CommonHelper.dp2px(this.mContext, 2);
        this.view_consumer.setLayoutParams(layoutParams);
        this.view_consumer.setBackgroundResource(R.color.colorAccent);

        layoutParams = view_recharge.getLayoutParams();
        layoutParams.height = CommonHelper.dp2px(this.mContext, 1);
        this.view_recharge.setLayoutParams(layoutParams);
        this.view_recharge.setBackgroundResource(R.color.light_gray);
    }

    /**
     *
     */
    private void rechargeTab() {
        this.tv_recharge.setTextColor(this.getResources().getColor(R.color.colorAccent));
        this.tv_consumer.setTextColor(this.getResources().getColor(R.color.color_999999));
        ViewGroup.LayoutParams layoutParams = view_recharge.getLayoutParams();
        layoutParams.height = CommonHelper.dp2px(this.mContext, 2);
        this.view_recharge.setLayoutParams(layoutParams);
        this.view_recharge.setBackgroundResource(R.color.colorAccent);

        layoutParams = view_consumer.getLayoutParams();
        layoutParams.height = CommonHelper.dp2px(this.mContext, 1);
        this.view_consumer.setLayoutParams(layoutParams);
        this.view_consumer.setBackgroundResource(R.color.light_gray);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.USER_ACCOUNT_INFO, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                UIHelper.showLoading(mContext);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                UIHelper.hideLoading();
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, result.getMsg());
                    return;
                }
                AccountModel model = JSON.parseObject(result.getData(), AccountModel.class);
                tv_total_money.setText(Html.fromHtml(MessageFormat.format("<font color=\"#c62435\">{0}</font>元", df.format(model.getMoney()))));
                tv_total_consumer.setText(MessageFormat.format("(消费总金额：￥{0})", df.format(model.getBuy())));
                tv_total_recharge.setText(MessageFormat.format("(充值总金额：￥{0})", df.format(model.getRecharge())));
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyLog.e(TAG, s);
                UIHelper.hideLoading();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_recharge, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tv_item_recharge:
                UIHelper.startActivity(this.mContext, AccountRechargeActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
