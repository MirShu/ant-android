package com.myyg.ui.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.utils.AppManager;
import com.myyg.utils.UIHelper;

public class RegisterActivity extends BaseActivity {

    @Override
    public void initView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("快速注册");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_mobile_register:
                UIHelper.startActivity(this.mContext, MobileRegisterActivity.class);
                break;
            case R.id.ll_emaile_register:
                UIHelper.startActivity(this.mContext, EmailRegisterActivity.class);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                AppManager.getAppManager().finishActivity();
                AppManager.getAppManager().finishActivity(LoginActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
