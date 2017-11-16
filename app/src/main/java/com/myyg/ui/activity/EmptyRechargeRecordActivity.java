package com.myyg.ui.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.utils.UIHelper;

/**
 * Created  on 2016/5/25.
 */
public class EmptyRechargeRecordActivity extends BaseActivity {

    @Override
    public void initView() {
        setContentView(R.layout.activity_recharge_record_empty);
    }

    @Override
    public void initData() {
    }

    @Override
    public void fillView() {
        this.setToolBar("充值记录");
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        UIHelper.startActivity(this.mContext, RechargeRecordActivity.class);
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
                UIHelper.startActivity(this.mContext, RechargeRecordActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
