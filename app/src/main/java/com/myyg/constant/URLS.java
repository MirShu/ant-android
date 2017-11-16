package com.myyg.constant;

import com.myyg.R;
import com.myyg.base.BaseApplication;

import java.text.MessageFormat;

/**
 * Created by JOHN on 2016/4/3.
 */
public class URLS {
    /**
     * API基路径
     */
    public static final String BASE_URL = BaseApplication.getInstance().getString(R.string.base_api);

    /**
     * 异常日志上传
     */
    public static final String COMMON_APP_ERROR_LOG = MessageFormat.format("{0}/app/extra/crash_log", BASE_URL);

    /**
     * APP数据初始化
     */
    public static final String EXTRA_INIT = MessageFormat.format("{0}/app/extra/init", BASE_URL);

    /**
     * 获取首页Banner
     */
    public static final String EXTRA_BANNER = MessageFormat.format("{0}/app/extra/banner", BASE_URL);

    /**
     * 获取商品分类列表
     */
    public static final String EXTRA_CATEGORY = MessageFormat.format("{0}/app/extra/category", BASE_URL);

    /**
     * 用户登录
     */
    public static final String EXTRA_LOGIN = MessageFormat.format("{0}/app/userauth/login", BASE_URL);

    /**
     * 最新揭晓列表
     */
    public static final String GOODS_LATEST_LIST = MessageFormat.format("{0}/app/goods/latest_list", BASE_URL);

    /**
     * 用户全部参与列表
     */
    public static final String USER_JOIN_LIST = MessageFormat.format("{0}/app/user/join_list", BASE_URL);

    /**
     * 用户参与的已揭晓的列表
     */
    public static final String USER_OPENED_LIST = MessageFormat.format("{0}/app/user/opened_list", BASE_URL);

    /**
     * 用户参与的进行中的列表
     */
    public static final String USER_PROCESS_LIST = MessageFormat.format("{0}/app/user/process_list", BASE_URL);

    /**
     * 用户信息
     */
    public static final String USER_GET_INFO = MessageFormat.format("{0}/app/user/get_info", BASE_URL);

    /**
     * 获取推荐商品信息
     */
    public static final String GOODS_RECOMMEND_LIST = MessageFormat.format("{0}/app/goods/recommend_list", BASE_URL);

    /**
     * 购物专区商品列表
     */
    public static final String GOODS_SHOP_LIST = MessageFormat.format("{0}/app/goods/shop_list", BASE_URL);

    /**
     * 用户已晒单列表
     */
    public static final String USER_SHARE_LIST = MessageFormat.format("{0}/app/user/share_list", BASE_URL);

    /**
     * 用户未晒单列表
     */
    public static final String USER_UN_SHARE_LIST = MessageFormat.format("{0}/app/user/unshare_list", BASE_URL);

    /**
     * 用户中奖记录
     */
    public static final String USER_WIN_LIST = MessageFormat.format("{0}/app/user/win_list", BASE_URL);

    /**
     * 猜您喜欢
     */
    public static final String GOODS_RANDOM = MessageFormat.format("{0}/app/goods/random", BASE_URL);

    /**
     * 晒单列表
     */
    public static final String EXTRA_SHARE_LIST = MessageFormat.format("{0}/app/extra/share_list", BASE_URL);

    /**
     * 分类下的商品列表
     */
    public static final String GOODS_LISTS = MessageFormat.format("{0}/app/goods/lists", BASE_URL);

    /**
     * 发送验证码
     */
    public static final String USERAUTH_SEND_CODE = MessageFormat.format("{0}/app/userauth/send_code", BASE_URL);

    /**
     * 检查版本更新
     */
    public static final String EXTRA_VERSION = MessageFormat.format("{0}/app/extra/version", BASE_URL);

    /**
     * 用户注册
     */
    public static final String USERAUTH_REGITER = MessageFormat.format("{0}/app/userauth/register", BASE_URL);

    /**
     * 获取商品晒单列表
     */
    public static final String GOODS_SHARE_LIST = MessageFormat.format("{0}/app/goods/share_list", BASE_URL);

    /**
     * 收货地址列表
     */
    public static final String ADDRESS_ALL = MessageFormat.format("{0}/app/address/all", BASE_URL);


    /**
     * 新增一个收货地址
     */
    public static final String ADDRESS_ADD = MessageFormat.format("{0}/app/address/add", BASE_URL);

    /**
     * 用户更新头像
     */
    public static final String USER_UPLOAD_AVATAR = MessageFormat.format("{0}/app/user/update_avatar", BASE_URL);

    /**
     * 用户积分信息
     */
    public static final String USER_REWARD = MessageFormat.format("{0}/app/user/reward", BASE_URL);

    /**
     * 用户充值记录
     */
    public static final String USER_RECHARGE_LOG = MessageFormat.format("{0}/app/user/recharge_log", BASE_URL);

    /**
     * 用户提现记录
     */
    public static final String USER_CASH_LOG = MessageFormat.format("{0}/app/user/cash_log", BASE_URL);

    /**
     * 用户邀请列表
     */
    public static final String USER_FRIENDS = MessageFormat.format("{0}/app/user/friends", BASE_URL);

    /**
     * 用户提交提现
     */
    public static final String USER_ADD_CASH = MessageFormat.format("{0}/app/user/add_cash", BASE_URL);

    /**
     * 删除一个收货地址
     */
    public static final String ADDRESS_DELETE = MessageFormat.format("{0}/app/address/delete", BASE_URL);
    /**
     * 更新用户昵称
     */
    public static final String USER_UPDATE_USERNAME = MessageFormat.format("{0}/app/user/update_username", BASE_URL);

    /**
     * 获取商品详情信息
     */
    public static final String GOODS_GET_DETAILS = MessageFormat.format("{0}/app/goods/item", BASE_URL);

    /**
     * 商品参与记录
     */
    public static final String GOODS_SHOP_RECORDS = MessageFormat.format("{0}/app/goods/shop_records", BASE_URL);

    /**
     * 商品往期揭晓
     */
    public static final String GOODS_LAST_LOTTERY = MessageFormat.format("{0}/app/goods/last_lottery", BASE_URL);

    /**
     * 编辑收货地址
     */
    public static final String ADDRESS_UPDATE = MessageFormat.format("{0}/app/address/update", BASE_URL);

    /**
     * 购物车中商品的信息
     */
    public static final String GOODS_CART_ITEM = MessageFormat.format("{0}/app/goods/cart_item", BASE_URL);

    /**
     * 用户提交订单
     */
    public static final String USER_PAY_SUBMIT = MessageFormat.format("{0}/app/user/pay_submit", BASE_URL);

    /**
     * 用户中奖确认页
     */
    public static final String USER_WIN_DETAIL = MessageFormat.format("{0}/app/user/win_detail", BASE_URL);

    /**
     * 按关键字进行搜索
     */
    public static final String GOODS_SEARCH = MessageFormat.format("{0}/app/goods/search", BASE_URL);

    /**
     * 添加晒单
     */
    public static final String USER_ADD_SHARE = MessageFormat.format("{0}/app/user/add_share", BASE_URL);


    /**
     * 获取其他用户信息
     */
    public static final String MEMBER_GET_INFO = MessageFormat.format("{0}/app/member/get_info", BASE_URL);

    /**
     * 其他用户中奖列表
     */
    public static final String MEMBER_WIN_LIST = MessageFormat.format("{0}/app/member/win_list", BASE_URL);

    /**
     * 其他用户参与列表
     */
    public static final String MEMBER_JOIN_LIST = MessageFormat.format("{0}/app/member/join_list", BASE_URL);

    /**
     * 其他用户晒单列表
     */
    public static final String MEMBER_SHARE_LIST = MessageFormat.format("{0}/app/member/share_list", BASE_URL);

    /**
     * 消息列表
     */
    public static final String USER_NOTICE_LIST = MessageFormat.format("{0}/app/user/notice_list", BASE_URL);

    /**
     * 获取未读消息总数
     */
    public static final String USER_NOTICE_COUNT = MessageFormat.format("{0}/app/user/notice_count", BASE_URL);

    /**
     * 读取消息
     */
    public static final String USER_READ_NOTICE = MessageFormat.format("{0}/app/user/notice_status", BASE_URL);

    /**
     * 验证手机号码
     */
    public static final String USERAUTH_ACCOUNT_VALID = MessageFormat.format("{0}/app/userauth/account_valid", BASE_URL);

    /**
     * 验证验证码
     */
    public static final String USERAUTH_CODE_VALID = MessageFormat.format("{0}/app/userauth/code_valid", BASE_URL);

    /**
     * 第三方登录
     */
    public static final String USERAUTH_OAUTH_LOGIN = MessageFormat.format("{0}/app/userauth/oauth_login", BASE_URL);

    /**
     * 修改密码
     */
    public static final String USER_CHANGE_PASSWORD = MessageFormat.format("{0}/app/user/change_password", BASE_URL);

    /**
     * 修改手机号
     */
    public static final String USER_MODIFY_MOBILE = MessageFormat.format("{0}/app/user/modify_mobile", BASE_URL);

    /**
     * 用户充值或支付
     */
    public static final String USER_RECHARGE = MessageFormat.format("{0}/app/user/recharge", BASE_URL);

    /**
     * 支付验证
     */
    public static final String USER_PAY_VERIFY = MessageFormat.format("{0}/app/user/pay_verify", BASE_URL);

    /**
     * 用户中奖未读列表
     */
    public static final String USER_WINNING = MessageFormat.format("{0}/app/user/winning", BASE_URL);

    /**
     * 设置用户中奖通知已读
     */
    public static final String USER_SETTING_WINNING_READ = MessageFormat.format("{0}/app/user/set_read", BASE_URL);

    /**
     * 用户佣金明细
     */
    public static final String USER_COMMISSION_DETAILS = MessageFormat.format("{0}/app/user/reward_detail", BASE_URL);

    /**
     * 佣金转化成余额
     */
    public static final String USER_REWARD_TO_ACCOUNT = MessageFormat.format("{0}/app/user/reward_to_account", BASE_URL);

    /**
     * 用户账户信息
     */
    public static final String USER_ACCOUNT_INFO = MessageFormat.format("{0}/app/user/account_info", BASE_URL);

    /**
     * 消费明细
     */
    public static final String USER_BUY_LOG = MessageFormat.format("{0}/app/user/buy_log", BASE_URL);
}

