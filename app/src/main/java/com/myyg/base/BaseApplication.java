package com.myyg.base;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.myyg.R;
import com.myyg.common.CrashHandler;
import com.myyg.common.ImageLoaderEx;
import com.myyg.constant.SharedKeys;
import com.myyg.constant.SysKeys;
import com.lidroid.xutils.http.RequestParams;
import com.myyg.model.UserModel;
import com.myyg.utils.SharedHelper;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by JOHN on 2016/4/3.
 */
public class BaseApplication extends Application {
    private static BaseApplication mInstance;
    private static String appToken = ""; // 用户登录token
    public static SharedPreferences appConfig;
    private static final String APP_CONFIG = "app_config";
    private static UserModel user = null;
    public static int WINDOW_WIDTH = 0;
    public static int WINDOW_HEIGHT = 0;
    private static List<String> listHotKeyword;

    @Override
    public void onCreate() {
        super.onCreate();
        this.initJPush();
        this.initSysData();
        this.initCrashHandler();
        this.initImageLoader();
    }

    /**
     * 初始化极光消息推送
     */
    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        user = getUser();
        if (user != null && !TextUtils.isEmpty(user.getUid())) {
            JPushInterface.setAlias(this, user.getUid(), null);
        }
    }

    /**
     * 初始化App参数
     */
    private void initSysData() {
        this.mInstance = this;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        WINDOW_WIDTH = dm.widthPixels;// 获取屏幕分辨率宽度
        WINDOW_HEIGHT = dm.heightPixels;
        this.appConfig = getSharedPreferences(APP_CONFIG, MODE_PRIVATE);
    }

    /**
     * 当前实例
     *
     * @return
     */
    public static BaseApplication getInstance() {
        return mInstance;
    }

    /**
     * 初始化程序崩溃处理
     */
    private void initCrashHandler() {
        try {
            String isTest = getResources().getString(R.string.is_test);
            // 判断是否为测试环境，如果为测试环境则不开启系统崩溃日志记录
            if (isTest.equals("true")) {
                return;
            }
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        } catch (Exception ex) {

        }
    }

    /**
     * 初始化图片缓存
     */
    private void initImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LruMemoryCache(4 * 1024 * 1024))
                .memoryCacheSize(4 * 1024 * 1024)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiskCache(ImageLoaderEx.getCacheDir(this)))
                .diskCacheFileCount(500).build();
        ImageLoader.getInstance().init(configuration);
    }

    /**
     * @return
     */
    public static RequestParams getParams() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/x-www-form-urlencoded");
        params.addHeader("myyg-sign", "0BA2E855E4240099FFD0620916BC8197");
        appToken = getToken();
        if (!TextUtils.isEmpty(appToken)) {
            params.addHeader("myyg-token", appToken);
        }
        return params;
    }

    /**
     * 获取登录Token
     *
     * @return
     */
    public static String getToken() {
        String token = appToken;
        if (TextUtils.isEmpty(token)) {
            token = appConfig.getString(SysKeys.SHARED_USER_TOKEN, "");
        }
        return token;
    }

    /**
     * @param token
     */
    public static void setToken(String token) {
        SharedPreferences.Editor editor = appConfig.edit();
        editor.putString(SysKeys.SHARED_USER_TOKEN, token);
        editor.commit();
    }

    /**
     * @param model
     */
    public static void sharedLogin(UserModel model) {
        if (TextUtils.isEmpty(model.getQrcode()) && user != null) {
            model.setQrcode(user.getQrcode());
        }
        user = model;
        appToken = model.getToken();
        if (TextUtils.isEmpty(appToken)) {
            appToken = getToken();
        }
        SharedPreferences.Editor editor = appConfig.edit();
        editor.putString(SysKeys.SHARED_USER_TOKEN, appToken);
        editor.putString(SysKeys.SHARED_USER_ID, user.getUid());
        editor.putString(SysKeys.SHARED_USER_USERNAME, user.getUsername());
        editor.putString(SysKeys.SHARED_USER_EMAIL, user.getEmail());
        editor.putString(SysKeys.SHARED_USER_MOBILE, user.getMobile());
        editor.putString(SysKeys.SHARED_USER_MONEY, user.getMoney());
        editor.putString(SysKeys.SHARED_USER_AVATAR, user.getAvatar());
        editor.putString(SysKeys.SHARED_USER_LEVEL, user.getLevel());
        editor.putString(SysKeys.SHARED_USER_QRCODE, user.getQrcode());
        editor.putString(SysKeys.SHARED_USER_SCORE, user.getScore());
        editor.putString(SysKeys.SHARED_USER_LINK, user.getLink());
        editor.commit();
    }

    /**
     * 设置本地登录信息
     *
     * @param key
     * @param value
     */
    public static void sharedPut(String key, String value) {
        user = null;
        SharedPreferences.Editor editor = appConfig.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 清除本地保存的登录信息
     */
    public static void cleanLogin() {
        user = null;
        appToken = "";
        SharedPreferences.Editor editor = appConfig.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public static UserModel getUser() {
        try {
            if (user != null) {
                return user;
            }
            appToken = appConfig.getString(SysKeys.SHARED_USER_TOKEN, "");
            if (TextUtils.isEmpty(appToken)) {
                return null;
            }
            user = new UserModel();
            user.setMobile(appConfig.getString(SysKeys.SHARED_USER_MOBILE, ""));
            user.setToken(appToken);
            user.setUid(appConfig.getString(SysKeys.SHARED_USER_ID, ""));
            user.setAvatar(appConfig.getString(SysKeys.SHARED_USER_AVATAR, ""));
            user.setEmail(appConfig.getString(SysKeys.SHARED_USER_EMAIL, ""));
            user.setUsername(appConfig.getString(SysKeys.SHARED_USER_USERNAME, ""));
            user.setMoney(appConfig.getString(SysKeys.SHARED_USER_MONEY, ""));
            user.setLevel(appConfig.getString(SysKeys.SHARED_USER_LEVEL, ""));
            user.setQrcode(appConfig.getString(SysKeys.SHARED_USER_QRCODE, ""));
            user.setScore(appConfig.getString(SysKeys.SHARED_USER_SCORE, "0"));
            user.setLink(appConfig.getString(SysKeys.SHARED_USER_LINK, ""));
            return user;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 获取热门搜索关键字
     *
     * @return
     */
    public static List<String> getHotKeyword() {
        if (listHotKeyword == null || listHotKeyword.size() == 0) {
            listHotKeyword = JSON.parseArray(SharedHelper.getString(SharedKeys.HOT_KEYWORD), String.class);
        }
        if (listHotKeyword == null) {
            listHotKeyword = new ArrayList<>();
        }
        return listHotKeyword;
    }
}
