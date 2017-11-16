package com.myyg.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.utils.MyLog;
import com.myyg.utils.SharedHelper;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shiyuankao on 2015-09-01.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    // 标签
    public static final String TAG = CrashHandler.class.getSimpleName();

    // 系统错误日志标签
    public static final String SYSTEM_ERROR = "System_Error";

    // 平台类型（1.Android 2.IOS）
    public static final int PLATFORM_TYPE = 1;

    // APP类型（1.太米）
    public static final int APP_TYPE = 1;

    //系统默认的UncaughtException处理类
    private UncaughtExceptionHandler defaultHandler;

    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();

    // 程序的Context对象
    private Context context;

    //用来存储设备信息和异常信息
    private Map<String, Object> hashMap = new HashMap<>();

    // 默认构造函数
    private CrashHandler() {

    }

    // 获取当前实例
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    // 初始化
    public void init(Context context) {
        this.context = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 初始化时，提交上次异常信息
        this.commitError();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            boolean result = handleException(ex);
            if (!result && defaultHandler != null) {
                //如果用户没有处理则让系统默认的异常处理器来处理
                defaultHandler.uncaughtException(thread, ex);
                return;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "ERROR : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } catch (Exception e) {

        }
    }

    // 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
    private boolean handleException(Throwable ex) {
        try {
            if (ex == null) {
                return false;
            }
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(context, "很抱歉，程序出现异常，即将退出", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();
            // 收集设备信息
            this.collectDeviceInfo(context);
            // 收集异常信息
            this.collectErrorInfo(ex);
            // 将异常信息保存至共享文件中
            hashMap.put("PlatformType", PLATFORM_TYPE);
            hashMap.put("AppType", APP_TYPE);
            SharedHelper.put(SYSTEM_ERROR, JSON.toJSONString(hashMap));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 收集设备信息
    private void collectDeviceInfo(Context ctx) {
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            String release = Build.VERSION.RELEASE;
            hashMap.put("Manufacturer", MessageFormat.format("{0},{1}", manufacturer.replaceAll("\"", ""), model));
            hashMap.put("OSVersion", release);
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "" : pi.versionName;
                String versionCode = pi.versionCode + "";
                hashMap.put("AppVersionName", versionName);
                hashMap.put("AppVersionNo", versionCode);
            }
            /*Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                hashMap.put(field.getName(),field.get(null).toString());
            }*/
        } catch (Exception ex) {

        }
    }

    // 收集异常信息
    private void collectErrorInfo(Throwable ex) {
        try {
            Throwable throwable = ex.getCause();
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            while (throwable != null) {
                throwable.printStackTrace(printWriter);
                throwable = throwable.getCause();
            }
            printWriter.close();
            String strError = writer.toString();
            hashMap.put("ErrorLogContent", strError);
        } catch (Exception e) {

        }
    }

    // 向服务器提交错误信息
    public void commitError() {
        try {
            String strJson = SharedHelper.get(SYSTEM_ERROR, "").toString();
            if (TextUtils.isEmpty(strJson)) {
                return;
            }
            MyLog.i(TAG, strJson);
            HttpUtils http = new HttpUtils();
            RequestParams params = BaseApplication.getParams();
            params.addBodyParameter("data", strJson);
//            JSONObject json = JSON.parseObject(strJson);
//            Map<String, Object> map = JSON.toJavaObject(json, Map.class);
//            for (Map.Entry<String, Object> item : map.entrySet()) {
//                params.addBodyParameter(item.getKey(), String.valueOf(item.getValue()));
//            }
            http.send(HttpRequest.HttpMethod.POST, URLS.COMMON_APP_ERROR_LOG, params, new RequestCallBack<String>() {

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    MessageResult result = MessageResult.parse(responseInfo.result);
                    if (result.getCode() == SysStatusCode.SUCCESS) {
                        SharedHelper.remove(SYSTEM_ERROR);
                    }
                    Log.i(TAG, result.getMsg());
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    MyLog.e(TAG, s);
                }
            });
        } catch (Exception ex) {

        }
    }
}
