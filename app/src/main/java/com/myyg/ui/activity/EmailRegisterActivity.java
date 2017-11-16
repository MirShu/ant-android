package com.myyg.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.myyg.R;
import com.myyg.base.BaseActivity;

public class EmailRegisterActivity extends BaseActivity {

    @Override
    public void initView() {
        setContentView(R.layout.activity_email_register);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("快速注册");

    }
}
