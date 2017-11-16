package com.myyg.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.myyg.R;
import com.myyg.ui.activity.MainActivity;
import com.myyg.utils.AppManager;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by JOHN on 2016/4/3.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = BaseActivity.class.getSimpleName();
    public Activity mContext;
    public boolean isTest = true;
    private String className;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        this.isTest = Boolean.parseBoolean(getResources().getString(R.string.is_test));
        this.mContext = this;
        this.initView();
        this.initData();
        this.fillView();
        this.bindListener();
        this.className = getClass().getSimpleName();
        this.TAG = className;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!this.isTest) {
            MobclickAgent.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!this.isTest) {
            MobclickAgent.onPause(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化视图
     */
    public abstract void initView();

    /**
     * 加载数据
     */
    public abstract void initData();

    /**
     * 填充视图数据
     */
    public abstract void fillView();

    /**
     * 绑定事件
     */
    public void bindListener() {

    }

    /**
     *
     */
    protected void setToolBar(String title) {
        this.mToolbar = (Toolbar) findViewById(R.id.bar_title);
        this.setSupportActionBar(this.mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_action_back);
        //actionBar.setShowHideAnimationEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        TextView tv_title = (TextView) this.mToolbar.findViewById(R.id.tv_title);
        tv_title.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onGoBack(null);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回
     *
     * @param view
     */
    public void onGoBack(View view) {
        this.finish();
    }

    /**
     * 点击事件（在子类中重写具体业务实现）
     *
     * @param view
     */
    public void onClick(View view) {

    }

    /**
     * 监测点击返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !className.equals(MainActivity.class.getSimpleName())) {
            this.onGoBack(null);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}


