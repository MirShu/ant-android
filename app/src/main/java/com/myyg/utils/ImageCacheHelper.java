package com.myyg.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jakewharton.disklrucache.DiskLruCache;
import com.myyg.base.BaseApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shiyuankao on 2016/3/14.
 */
public class ImageCacheHelper implements Handler.Callback {
    private final static String TAG = ImageCacheHelper.class.getSimpleName();

    /**
     * 声明DiskLruCache硬盘缓存对象
     */
    private static DiskLruCache diskLruCache;

    /**
     * 下载Image的线程池
     */
    private static ExecutorService mImageThreadPool = null;

    /**
     *
     */
    static {
        // 初始化硬盘缓存DiskLruCahce
        // 获取硬盘缓存路径,参数二为所在缓存路径的文件夹名称
        File directory = FileHelper.getDiskCacheDir(BaseApplication.getInstance(), "bitmap");
        if (!directory.exists()) {
            // 若文件夹不存在，建立文件夹
            directory.mkdirs();
        }
        int appVersion = CommonHelper.getPackage().versionCode;
        // 参数1：缓存文件路径
        // 参数2：系统版本号
        // 参数3：一个缓存路径对于几个文件
        // 参数4：缓存空间大小（字节）
        try {
            diskLruCache = DiskLruCache.open(directory, appVersion, 1, 100 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     *
     * @return
     */
    private static ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    //为了下载图片更加的流畅，我们用了2个线程来下载图片
                    mImageThreadPool = Executors.newFixedThreadPool(2);
                }
            }
        }
        return mImageThreadPool;
    }

    /**
     * 从缓存中获取图片，如果不存在则从网络下载并写入缓存中
     *
     * @param url      图片下载地址
     * @param listener 图片下载成功或失败事件
     * @return
     */
    public static Bitmap getCacheBitmap(final String url, final OnImageCacheListener listener) {
        try {
            Bitmap bitmap = null;
            if (TextUtils.isEmpty(url)) {
                return bitmap;
            }
            final String cacheKey = CommonHelper.toMd5(url);
            DiskLruCache.Snapshot snapshot = diskLruCache.get(cacheKey);
            if (snapshot != null) {
                bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
            }
            if (bitmap != null) {
                return bitmap;
            }
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (listener != null) {
                        listener.onDownloadComplete((Bitmap) msg.obj, url);
                    }
                }
            };
            ExecutorService task = getThreadPool();
            task.execute(new Runnable() {
                @Override
                public void run() {
                    OutputStream outputStream = null;
                    try {
                        DiskLruCache.Editor editor = diskLruCache.edit(cacheKey);
                        if (editor != null) {
                            outputStream = editor.newOutputStream(0);
                            boolean result = downloadUrlToStream(url, outputStream);
                            if (result) {
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                            DiskLruCache.Snapshot snapshot = diskLruCache.get(cacheKey);
                            if (snapshot != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                                if (bitmap != null) {
                                    Message msg = handler.obtainMessage();
                                    msg.obj = bitmap;
                                    handler.sendMessage(msg);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 获取Bitmap, 从缓存中（本地SD卡）获取
     *
     * @param url
     * @return
     */
    public static Bitmap getCacheBitmap(String url) {
        try {
            Bitmap bitmap = null;
            if (TextUtils.isEmpty(url)) {
                return bitmap;
            }
            String cacheKey = CommonHelper.toMd5(url);
            DiskLruCache.Snapshot snapshot = diskLruCache.get(cacheKey);
            if (snapshot != null) {
                bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
            } else {
                getCacheBitmap(url, null);
            }
            return bitmap;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 根据图片URL地址下载图片,成功返回true，失败false
     *
     * @param urlString
     * @param outputStream
     * @return
     */
    private static boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 取消正在下载的任务
     */
    public static synchronized void cancelTask() {
        if (mImageThreadPool != null) {
            mImageThreadPool.shutdownNow();
            mImageThreadPool = null;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }


    /**
     * 图片缓存事件定义
     */
    public interface OnImageCacheListener {
        /**
         * 图片下载完成
         *
         * @param bitmap
         * @param url
         */
        void onDownloadComplete(Bitmap bitmap, String url);

        /**
         * 图片下载失败
         *
         * @param url
         */
        void onDownloadFailure(String url);
    }
}
