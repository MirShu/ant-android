package com.myyg.photopicker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

import com.myyg.photopicker.PhotoPickerActivity;

/**
 * Created by donglua on 15/7/2.
 */
public class PhotoPickerIntent extends Intent {

    public PhotoPickerIntent() {
    }

    public PhotoPickerIntent(Intent o) {
        super(o);
    }

    public PhotoPickerIntent(String action) {
        super(action);
    }

    public PhotoPickerIntent(String action, Uri uri) {
        super(action, uri);
    }

    public PhotoPickerIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public PhotoPickerIntent(Context packageContext) {
        super(packageContext, PhotoPickerActivity.class);
    }

    public void setPhotoCount(int photoCount) {
        this.putExtra(PhotoPickerActivity.EXTRA_MAX_COUNT, photoCount);
    }

    public void setShowCamera(boolean showCamera) {
        this.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
    }

    public void setShowGif(boolean showGif) {
        this.putExtra(PhotoPickerActivity.EXTRA_SHOW_GIF, showGif);
    }

    /**
     * 过滤不显示的目录
     *
     * @param listDir
     */
    public void setFilterDir(ArrayList<String> listDir) {
        this.putExtra(PhotoPickerActivity.FILTER_DIR, listDir);
    }

    public void setShowTips(boolean isShowTips) {
        this.putExtra(PhotoPickerActivity.EXTRA_SHOT_TIPS, isShowTips);
    }

    public void setClolor(String color) {
        this.putExtra(PhotoPickerActivity.THEME_COLOR, color);
    }

    public void setShowDone(boolean isShowDone) {
        this.putExtra(PhotoPickerActivity.EXTRA_SHOW_DONE, isShowDone);
    }
}
