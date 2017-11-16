package com.myyg.ui.activity;

import android.os.Bundle;

import com.myyg.R;
import com.myyg.base.BaseActivity;

/**
 * Created by Administrator on 2016/6/1.
 */
public class GraphicDetailsActivity extends BaseActivity {
    @Override
    public void initView() {
        setContentView(R.layout.activity_graphic_details);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        setToolBar("图文详情");
    }
}
