package com.myyg.utils;

import android.content.Context;

import com.myyg.R;
import com.myyg.base.BaseApplication;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by JOHN on 2016/8/9.
 */
public class WxHelper {
    private final static String TAG = WxHelper.class.getSimpleName();
    public final static String REQUEST_LOGIN_TAG = "request_lgoin_tag";
    private final static String SCOPE_USER_INFO = "snsapi_userinfo";
    private IWXAPI wxApi;
    private static WxHelper instance;
    private Context mContext;

    /**
     * @param context
     */
    private WxHelper(Context context) {
        this.mContext = context;
        String wxAppId = BaseApplication.getInstance().getResources().getString(R.string.wx_app_id);
        wxApi = WXAPIFactory.createWXAPI(context, wxAppId);
        wxApi.registerApp(wxAppId);
    }

    /**
     * @param context
     * @return
     */
    public static synchronized WxHelper getInstance(Context context) {
        if (instance == null) {
            instance = new WxHelper(context);
        }
        return instance;
    }

    /**
     * 获取API实例
     *
     * @return
     */
    public IWXAPI getApi() {
        return wxApi;
    }

    /**
     * 用户登录
     */
    public void wxLogin() {
        try {
            if (!wxApi.isWXAppInstalled()) {
                UIHelper.toastMessage(this.mContext, "请先安装微信客户端");
                return;
            }
            UIHelper.showLoading(this.mContext);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = SCOPE_USER_INFO;
            req.state = REQUEST_LOGIN_TAG;
            wxApi.sendReq(req);
        } catch (Exception ex) {
            MyLog.e(TAG, ex.getMessage());
            UIHelper.hideLoading();
        }
    }
}
