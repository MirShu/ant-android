package com.myyg.model;

import com.myyg.constant.SysEnums;

import java.io.Serializable;

/**
 * Created by shiyuankao on 2016/8/11.
 */
public class AuthLoginModel implements Serializable {
    private static final long serialVersionUID = 1362691795743846338L;

    private String openId;

    private String avatar;

    private String nickName;

    private SysEnums.EnumAuthLogin authLoginType;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public SysEnums.EnumAuthLogin getAuthLoginType() {
        return authLoginType;
    }

    public void setAuthLoginType(SysEnums.EnumAuthLogin authLoginType) {
        this.authLoginType = authLoginType;
    }
}
