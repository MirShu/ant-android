package com.myyg.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
import com.myyg.constant.SharedKeys;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.db.DbHelper;
import com.myyg.model.AreaModel;
import com.myyg.model.BannerModel;
import com.myyg.model.InitModel;
import com.myyg.model.MessageResult;
import com.myyg.utils.MyLog;
import com.myyg.utils.SharedHelper;
import com.myyg.utils.UIHelper;

import java.util.HashMap;
import java.util.List;

public class StartActivity extends BaseActivity implements Handler.Callback {

    /**
     *
     */
    private Handler mHandler;

    @Override
    public void initView() {
        setContentView(R.layout.activity_start);
    }

    @Override
    public void initData() {
        this.mHandler = new Handler(this);
    }

    @Override
    public void fillView() {
        this.mHandler.sendEmptyMessageDelayed(0, 3000);
        this.loadInit();
        new Handler().post(() -> {
            List<AreaModel> listArea = DbHelper.getAreaByParentId(19);
            if (listArea != null) {
                for (AreaModel item : listArea) {
                    MyLog.i(TAG, "AreaId:" + item.getAreaId() + "  AreaName:" + item.getAreaName());
                }
            }
        });
    }

    /**
     *
     */
    private void loadInit() {
        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(0);
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.EXTRA_INIT, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() == SysStatusCode.SUCCESS) {
                    InitModel initModel = InitModel.parse(message.getData());
                    SharedHelper.put(SharedKeys.HOME_BANNER, JSON.toJSONString(initModel.getBanner()));
                    SharedHelper.put(SharedKeys.HOME_GOODS_CATEGORY, JSON.toJSONString(initModel.getCategory()));
                    SharedHelper.put(SharedKeys.HOT_KEYWORD, JSON.toJSONString(initModel.getKeyword()));
                    HashMap<String, Object> config = initModel.getConfig();
                    for (String key : config.keySet()) {
                        String value = String.valueOf(config.get(key));
                        SharedHelper.put(key, value);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        UIHelper.startActivity(this.mContext, MainActivity.class);
        //UIHelper.startActivity(this.mContext, MyygTestActivity.class);
        return false;
    }

    @Override
    protected void onPause() {
        StartActivity.this.finish();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
