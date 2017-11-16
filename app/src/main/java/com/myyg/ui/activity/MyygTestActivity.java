package com.myyg.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.widget.TimeTextView;

import cn.iwgang.countdownview.CountdownView;

public class MyygTestActivity extends BaseActivity {
    private TimeTextView ttv_time;

    private CountdownView cv_test;

    @Override
    public void initView() {
        this.setContentView(R.layout.activity_myyg_test);
        this.ttv_time = (TimeTextView) this.findViewById(R.id.ttv_time);
        this.cv_test = (CountdownView) this.findViewById(R.id.cv_test);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.ttv_time.setTime(60000, 1000);
        this.cv_test.start(995550000);
    }
}
