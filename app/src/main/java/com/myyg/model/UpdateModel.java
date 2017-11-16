package com.myyg.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/6/10.
 */
public class UpdateModel implements Serializable {
    private static final long serialVersionUID = 875569264006646152L;

    private String AppName;
    private int VersionCode;
    private String FilePath;
    private String IsForced;
    private String ReleaseContent;
    private String CreateTime;

    public UpdateModel() {
        super();
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getIsForced() {
        return IsForced;
    }

    public void setIsForced(String isForced) {
        IsForced = isForced;
    }

    public String getReleaseContent() {
        return ReleaseContent;
    }

    public void setReleaseContent(String releaseContent) {
        ReleaseContent = releaseContent;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public static UpdateModel parse(String result) {
        return JSON.parseObject(result, UpdateModel.class);
    }
}
