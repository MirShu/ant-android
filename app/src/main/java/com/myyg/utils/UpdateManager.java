package com.myyg.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.base.BaseApplication;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.UpdateModel;
import com.myyg.ui.activity.SettingActivity;
import com.myyg.ui.view.CommonDialog;
import com.myyg.ui.view.CustomProgressDialog;
import com.myyg.ui.view.DoubleButtonDialog;

/**
 * Created by JOHN on 2015/11/30.
 */
public class UpdateManager {
    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DOWN_FAIL = 3;

    private static final int DIALOG_TYPE_LATEST = 0;
    private static final int DIALOG_TYPE_FAIL = 1;
    // 获取当前版本出错
    private static final int DIALOG_TYPE_LOCALHOS = 2;

    private static UpdateManager updateManager;

    private Context mContext;
    // 下载对话框
    private Dialog downloadDialog;
    // '已经是最新' 或者 '无法获取最新版本' 的对话框
    private Dialog latestOrFailDialog;
    // 进度条
    private ProgressBar mProgress;
    // 显示下载数值
    private TextView mProgressText;
    // 查询动画
    private CustomProgressDialog mProDialog;
    // 进度值
    private int progress;
    // 下载线程
    private Thread downLoadThread;
    // 终止标记
    private boolean interceptFlag;
    // //提示语
    // private String updateMsg = "";
    // 返回的安装包url
    private String apkUrl = "";
    // 下载包保存路径
    private String savePath = "";
    // apk保存完整路径
    private String apkFilePath = "";
    // 临时下载文件路径
    private String tmpFilePath = "";
    // 下载文件大小
    private String apkFileSize;
    // 已下载文件大小
    private String tmpFileSize;
    private UpdateModel mUpdate;
    private BaseApplication application;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    mProgressText.setText(tmpFileSize + "/" + apkFileSize);
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    installApk();
                    break;
                case DOWN_NOSDCARD:
                    downloadDialog.dismiss();
                    UIHelper.toastMessage(mContext, "无法下载安装文件，请检查SD卡是否挂载");
                    break;
                case DOWN_FAIL:
                    downloadDialog.dismiss();
                    UIHelper.toastMessage(mContext, "无法下载安装文件，请检查网络或稍后重试");
                    break;
            }
        }

        ;
    };

    public static UpdateManager getUpdateManager() {
        if (updateManager == null) {
            updateManager = new UpdateManager();
        }
        updateManager.interceptFlag = false;
        return updateManager;
    }

    /**
     * 检查App更新
     *
     * @param context
     * @param isShowMsg 是否显示提示消息
     */
    public void checkAppUpdate(final Context context, final boolean isShowMsg, final boolean isfirst) {
        this.mContext = context;
        application = (BaseApplication) mContext.getApplicationContext();
        if (isShowMsg && !isfirst) {
            UIHelper.showLoading(mContext, "正在检测最新版本...");
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.EXTRA_VERSION, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            // 进度条对话框不显示 - 检测结果也不显示
                            if (mProDialog != null && !mProDialog.isShowing()) {
                                return;
                            }
                            // 关闭并释放释放进度条对话框
                            if (isShowMsg && mProDialog != null) {
                                mProDialog.dismiss();
                                mProDialog = null;
                            }
                            MessageResult result = MessageResult.parse(responseInfo.result);
                            if (TextUtils.isEmpty(result.getData())) {
                                if (isShowMsg) {
                                    showLatestOrFailDialog(DIALOG_TYPE_LATEST, isfirst);
                                }
                                return;
                            }
                            mUpdate = UpdateModel.parse(result.getData());
                            PackageInfo pi = getCurrentVersion();
                            if (pi == null) {
                                showLatestOrFailDialog(DIALOG_TYPE_LOCALHOS, isfirst);
                                return;
                            }
                            // 判断本地使用版本和服务器版本
                            if (pi.versionCode >= mUpdate.getVersionCode()) {
                                showLatestOrFailDialog(DIALOG_TYPE_LATEST, isfirst);
                                return;
                            }
                            apkUrl = mUpdate.getFilePath();
                            // 判断是否为强制更新
                            if (mUpdate.getIsForced().equals("true")) {
                                showForceNoticeDialog(mUpdate);
                            } else {
                                showNoticeDialog(mUpdate);
                            }
                        } catch (Exception e) {
                            showLatestOrFailDialog(DIALOG_TYPE_FAIL, isfirst);
                        } finally {
                            UIHelper.hideLoading();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        UIHelper.hideLoading();
                        showLatestOrFailDialog(DIALOG_TYPE_FAIL, isfirst);
                    }
                });
    }

    /**
     * 显示'已经是最新'或者'无法获取版本信息'对话框
     */
    private void showLatestOrFailDialog(int dialogType, final boolean isfirst) {
        if (latestOrFailDialog != null) {
            // 关闭并释放之前的对话框
            latestOrFailDialog.dismiss();
            latestOrFailDialog = null;
        }
        if (isfirst) return;
        Builder builder = new Builder(mContext);
        builder.setTitle("系统提示");
        if (dialogType == DIALOG_TYPE_LATEST) {
            UIHelper.startActivity((Activity) mContext, SettingActivity.class);
            return;
            //builder.setMessage("您当前已是最新版本!");
        } else if (dialogType == DIALOG_TYPE_FAIL) {
            builder.setMessage("无法获取版本更新信息");
        } else if (dialogType == DIALOG_TYPE_LOCALHOS) {
            builder.setMessage("无法获取用户当前使用版本信息");
        }
        if (mProDialog != null) {
            mProDialog.dismiss();
        }
        builder.setPositiveButton("确定", null);
        latestOrFailDialog = builder.create();
        latestOrFailDialog.show();
    }

    /**
     * 获取当前客户端版本信息
     */
    private PackageInfo getCurrentVersion() {
        try {
            PackageInfo pack = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return pack;
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * 显示版本更新通知对话框
     * 普通升级
     */
    private void showNoticeDialog(UpdateModel update) {
        String content = update.getReleaseContent();
        content = this.getString(content);
        DoubleButtonDialog dialog = new DoubleButtonDialog(mContext);
        dialog.setCancelable(false);
        dialog.setContent("发现新版本", content);
        dialog.setButtonText("稍后再说", "现在更新");
        dialog.setContextGravity(Gravity.LEFT);
        dialog.setButtonListener(new DoubleButtonDialog.OnButtonListener() {
            @Override
            public void onLeftListener(Dialog obj) {
                obj.dismiss();
            }

            @Override
            public void onRightListener(Dialog obj) {
                obj.dismiss();
                showDownloadDialog();
            }
        });
        dialog.onShow();
    }

    /**
     * 强制升级
     */
    private void showForceNoticeDialog(UpdateModel update) {
        String content = update.getReleaseContent();
        content = this.getString(content);
        CommonDialog dialog = new CommonDialog(mContext, "现在更新");
        dialog.setCancelable(false);
        dialog.setContent("发现新版本", content);
        dialog.setButtonListener(new CommonDialog.OnCommonButtonListener() {
            @Override
            public void onSubmitListener(Dialog obj) {
                showDownloadDialog();
            }
        });
        dialog.onShow();
    }

    /**
     * 字符处理
     *
     * @param data
     * @return
     */
    private String getString(String data) {
        if (!TextUtils.isEmpty(data)) {
            data = "-" + data;
            return data.replace(";", "\n-");
        } else {
            return "";
        }
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadDialog() {
        downloadDialog = new Dialog(mContext, R.style.dialog_theme);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.view_update_progress, null);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                downloadDialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadDialog.dismiss();
                interceptFlag = true;
            }
        });
        mProgress = (ProgressBar) view.findViewById(R.id.update_progress);
        mProgressText = (TextView) view.findViewById(R.id.update_progress_text);
        downloadDialog.setContentView(view);
        downloadDialog.setCancelable(false);
        downloadDialog.show();
        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                String apkName = "51bj_" + mUpdate.getAppName() + ".apk";
                String tmpApk = "51bj_" + mUpdate.getAppName() + ".tmp";
                // 判断是否挂载了SD卡

                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    savePath = MessageFormat.format("{0}{1}hn{2}apk{3}", Environment.getExternalStorageDirectory().getAbsolutePath(), File.separator, File.separator, File.separator);
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    apkFilePath = savePath + apkName;
                    tmpFilePath = savePath + tmpApk;
                }
                // 没有挂载SD卡，无法下载文件
                if (apkFilePath == null || apkFilePath == "") {
                    mHandler.sendEmptyMessage(DOWN_NOSDCARD);
                    return;
                }
                File ApkFile = new File(apkFilePath);
                // 是否已下载更新文件
                if (ApkFile.exists()) {
                    downloadDialog.dismiss();
                    installApk();
                    return;
                }
                // 输出临时下载文件
                File tmpFile = new File(tmpFilePath);
                FileOutputStream fos = new FileOutputStream(tmpFile);

                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                // 显示文件大小格式：2个小数点显示
                DecimalFormat df = new DecimalFormat("0.00");
                // 进度条下面显示的总文件大小
                apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    // 进度条下面显示的当前下载文件大小
                    tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                    // 当前进度值
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成 - 将临时下载文件转成APK文件
                        if (tmpFile.renameTo(ApkFile)) {
                            // 通知安装
                            mHandler.sendEmptyMessage(DOWN_OVER);
                        }
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(DOWN_FAIL);
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(DOWN_FAIL);
            }
        }
    };

    /**
     * 下载apk
     *
     * @param
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     *
     * @param
     */
    private void installApk() {
        File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }
}