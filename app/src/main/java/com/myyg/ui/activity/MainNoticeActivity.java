package com.myyg.ui.activity;

import android.view.View;

import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;

import java.text.MessageFormat;

/**
 * Created by Administrator on 2016/5/29.
 */
public class MainNoticeActivity extends BaseActivity {
    @Override
    public void initView() {
        setContentView(R.layout.activity_main_notice);

    }

    @Override
    public void initData() {
    }

    @Override
    public void fillView() {
        setToolBar("通知-蚂蚁云购");
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.rl_common_problem:
                UIHelper.startActivity(this.mContext, NoticeActivity.class);
                break;
            case R.id.rl_version_information:
                String version = CommonHelper.getPackage().versionName;
                UIHelper.toastMessage(this.mContext, "当前为最新版本V" + version);
                break;
        }
    }
}
