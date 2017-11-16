package com.myyg.model;

import com.alibaba.fastjson.JSON;
import com.myyg.utils.MyLog;

import java.io.Serializable;

/**
 * Created by shiyuankao on 2016/6/3.
 */
public class BannerModel implements Serializable {
    private static final String TAG = BannerModel.class.getSimpleName();

    private static final long serialVersionUID = -1466413198198965258L;

    private String title;

    private String img;

    private String link;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @param result
     * @return
     */
    public static BannerModel parse(String result) {
        BannerModel banner = new BannerModel();
        try {
            banner = JSON.parseObject(result, BannerModel.class);
        } catch (Exception e) {
            String error = e.getMessage();
            MyLog.e(TAG, error);
        }
        return banner;
    }
}
