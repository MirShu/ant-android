package com.myyg.model;

import com.alibaba.fastjson.JSON;
import com.myyg.utils.MyLog;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/6/5.
 */
public class UserModel implements Serializable {
    private static final String TAG = UserModel.class.getSimpleName();

    private static final long serialVersionUID = -4025765892726377558L;

    private String uid;

    private String token;

    private String username;

    private String email;

    private String mobile;

    private String avatar;

    private String money;

    private String score;

    private String level;

    private String qrcode;

    private String reward;

    private String percent;

    private String name;

    private String link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
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
    public static UserModel parse(String result) {
        UserModel userModel = new UserModel();
        try {
            userModel = JSON.parseObject(result, UserModel.class);
        } catch (Exception e) {
            String error = e.getMessage();
            MyLog.e(TAG, error);
        }
        return userModel;
    }
}
