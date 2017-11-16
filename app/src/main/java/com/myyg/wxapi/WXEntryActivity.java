package com.myyg.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.constant.SysConstant;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;
import com.myyg.wxapi.model.WxTokenModel;
import com.myyg.wxapi.model.WxUserModel;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.text.MessageFormat;

/**
 * Created by JOHN on 2016/8/9.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private final static String TAG = WXEntryActivity.class.getSimpleName();
    public final static String WX_USER_INFO = "wx_user_info";
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String wxAppId = this.getResources().getString(R.string.wx_app_id);
        this.api = WXAPIFactory.createWXAPI(this, wxAppId, false);
        this.api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.errCode != BaseResp.ErrCode.ERR_OK) {
            UIHelper.hideLoading();
            MyLog.e(TAG, resp.errStr);
            WXEntryActivity.this.finish();
            return;
        }
        // 分享到微信
        if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {

        }
        // 微信登录
        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            Bundle bundle = getIntent().getExtras();
            SendAuth.Resp sendAuth = new SendAuth.Resp(bundle);
            MyLog.i(TAG, "微信Code：" + sendAuth.code);
            this.getToken(sendAuth.code);
        }
        WXEntryActivity.this.finish();
    }

    /**
     * 获取微信Token
     *
     * @param code
     */
    private void getToken(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code";
        url = MessageFormat.format(url, this.getString(R.string.wx_app_id), this.getString(R.string.wx_app_secret), code);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                //UIHelper.showLoading(WXEntryActivity.this, "正在登录,请稍等...");
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    WxTokenModel wxTokenModel = JSON.parseObject(responseInfo.result, WxTokenModel.class);
                    if (TextUtils.isEmpty(wxTokenModel.getAccess_token())) {
                        MyLog.e(TAG, wxTokenModel.getErrmsg());
                        return;
                    }
                    String access_token = wxTokenModel.getAccess_token();
                    String openid = wxTokenModel.getOpenid();
                    getUserInfo(access_token, openid);
                } catch (Exception ex) {
                    UIHelper.hideLoading();
                    MyLog.e(TAG, ex.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
            }
        });


    }

    /**
     * 获取用户基本信息
     *
     * @param access_token
     * @param openId
     */
    private void getUserInfo(String access_token, String openId) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token={0}&openid={1}";
        url = MessageFormat.format(url, access_token, openId);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MyLog.i(TAG, responseInfo.result);
                    WxUserModel wxUserModel = JSON.parseObject(responseInfo.result, WxUserModel.class);
                    if (wxUserModel.getErrcode() > 0) {
                        MyLog.e(TAG, wxUserModel.getErrmsg());
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setAction(SysConstant.ACTION_MYYG_RECEIVE_WX_LOGIN);
                    intent.putExtra(WX_USER_INFO, wxUserModel);
                    sendBroadcast(intent);
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {
                    UIHelper.hideLoading();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
            }
        });
    }
}
