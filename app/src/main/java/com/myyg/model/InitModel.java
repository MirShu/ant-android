package com.myyg.model;

import com.alibaba.fastjson.JSON;
import com.myyg.utils.MyLog;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JOHN on 2016/6/3.
 */
public class InitModel implements Serializable {
    private static final String TAG = InitModel.class.getSimpleName();

    private static final long serialVersionUID = -7437142728098491671L;

    private List<BannerModel> banner;

    private List<GoodsCategoryModel> category;

    private HashMap<String, Object> config;

    private List<String> keyword;

    public List<BannerModel> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerModel> banner) {
        this.banner = banner;
    }

    public List<GoodsCategoryModel> getCategory() {
        return category;
    }

    public void setCategory(List<GoodsCategoryModel> category) {
        this.category = category;
    }

    public HashMap<String, Object> getConfig() {
        return config;
    }

    public void setConfig(HashMap<String, Object> config) {
        this.config = config;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    /**
     * @param result
     * @return
     */
    public static InitModel parse(String result) {
        InitModel initModel = new InitModel();
        try {
            initModel = JSON.parseObject(result, InitModel.class);
        } catch (Exception e) {
            String error = e.getMessage();
            MyLog.e(TAG, error);
        }
        return initModel;
    }
}
