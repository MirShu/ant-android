package com.myyg.ui.activity;

import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysHtml;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;
import com.myyg.utils.UpdateManager;

import java.text.MessageFormat;

/**
 * Created by Administrator on 2016/5/31.
 */
public class SettingActivity extends BaseActivity {
    private TextView tv_version;
    private long cacheSize; //缓存大小
    private TextView tv_cachesize;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setting);
        this.tv_version = (TextView) this.findViewById(R.id.tv_version);
        this.tv_cachesize = (TextView) this.findViewById(R.id.tv_cachesize);
        this.tv_cachesize.setText(formateFileSize(cacheSize));
    }

    @Override
    public void initData() {
        String version = CommonHelper.getPackage().versionName;
        this.tv_version.setText(MessageFormat.format("V{0}", version));

    }

    @Override
    public void fillView() {
        this.setToolBar("设置");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_self_qadetails:
                Bundle bundle = new Bundle();
                bundle.putString(WebBrowseActivity.WEB_BROWSE_LINK_TAG, SysHtml.HTML_SELF_QADETAILS);
                UIHelper.startActivity(this.mContext, WebBrowseActivity.class, bundle);
                break;
            case R.id.rl_version:
                UpdateManager.getUpdateManager().checkAppUpdate(this, true, false);
                break;
            case R.id.rl_logout:
                this.logOut();
                break;
        }
    }

    /**
     * 登出
     */
    private void logOut() {
        BaseApplication.cleanLogin();
        UIHelper.startActivity(this.mContext, LoginActivity.class);
        setResult(RESULT_OK);
        this.finish();
    }

    /**
     * @param cachesize
     * @return
     */
    private String formateFileSize(long cachesize) {
        return Formatter.formatFileSize(SettingActivity.this, cachesize);
    }
}
