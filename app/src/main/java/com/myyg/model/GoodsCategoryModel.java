package com.myyg.model;

import com.alibaba.fastjson.JSON;
import com.myyg.utils.MyLog;

import java.io.Serializable;

/**
 * Created by shiyuankao on 2016/6/3.
 */
public class GoodsCategoryModel implements Serializable {
    private static final String TAG = GoodsCategoryModel.class.getSimpleName();

    private static final long serialVersionUID = -6123009935873432245L;

    private String cateid;

    private String parentid;

    private String name;

    private String catdir;

    private String url;

    private String order;

    public GoodsCategoryModel() {
    }

    public GoodsCategoryModel(String cateid, String name) {
        this.cateid = cateid;
        this.name = name;
    }

    public String getCateid() {
        return cateid;
    }

    public void setCateid(String cateid) {
        this.cateid = cateid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatdir() {
        return catdir;
    }

    public void setCatdir(String catdir) {
        this.catdir = catdir;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * @param result
     * @return
     */
    public static GoodsCategoryModel parse(String result) {
        GoodsCategoryModel model = new GoodsCategoryModel();
        try {
            model = JSON.parseObject(result, GoodsCategoryModel.class);
        } catch (Exception e) {
            String error = e.getMessage();
            MyLog.e(TAG, error);
        }
        return model;
    }
}
