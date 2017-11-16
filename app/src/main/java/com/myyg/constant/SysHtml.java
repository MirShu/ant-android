package com.myyg.constant;

import com.myyg.R;
import com.myyg.base.BaseApplication;

import java.text.MessageFormat;

/**
 * Created by JOHN on 2016/4/3.
 */
public class SysHtml {
    public static final String BASE_H5 = BaseApplication.getInstance().getString(R.string.base_h5);

    public static final String HTML_GOODS_DETAILS = MessageFormat.format("{0}/mobile/mobile/goodsdesc", BASE_H5);

    public static final String HTML_SHAIDAN_DETAILS = MessageFormat.format("{0}/mobile/shaidan/detail", BASE_H5);

    // 隐私政策
    public static final String HTML_PRIVACY_POLICY = MessageFormat.format("{0}/mobile/mobile/privacy_policy", BASE_H5);

    // 关于蚂蚁云购
    public static final String HTML_ABOUT_US = MessageFormat.format("{0}/mobile/mobile/about_us", BASE_H5);

    // 常见问题
    public static final String HTML_SELF_QADETAILS = MessageFormat.format("{0}/mobile/mobile/self_qadetails", BASE_H5);

    /**
     * 通知详情
     */
    public static final String HTML_NOTICE_DETAILS = MessageFormat.format("{0}/mobile/mobile/noticedetails", BASE_H5);

    /**
     * 计算结果
     */
    public static final String HTML_CALCULATION_RESULT = MessageFormat.format("{0}/mobile/mobile/calResult", BASE_H5);

    /**
     * 服务协议
     */
    public static final String HTML_SERVICE = MessageFormat.format("{0}/mobile/mobile/service", BASE_H5);

    /**
     * 参与详情
     */
    public static final String HTML_BUY_DETAILS = MessageFormat.format("{0}/mobile/user/buyDetail", BASE_H5);
}
