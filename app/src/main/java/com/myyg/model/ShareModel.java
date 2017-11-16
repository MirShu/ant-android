package com.myyg.model;

import java.io.Serializable;

/**
 * Created by shiyuankao on 2016/6/7.
 */
public class ShareModel implements Serializable {
    private static final String TAG = ShareModel.class.getSimpleName();

    private static final long serialVersionUID = 1282152604983434465L;

    private String id;

    private String user_id;

    private String qishu;

    private String title;

    private String sd_title;

    private String thumbs;

    private String content;

    private String photolist;

    private String time;

    private String avatar;

    private String username;

    private String email;

    private String mobile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getQishu() {
        return qishu;
    }

    public void setQishu(String qishu) {
        this.qishu = qishu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSd_title() {
        return sd_title;
    }

    public void setSd_title(String sd_title) {
        this.sd_title = sd_title;
    }

    public String getThumbs() {
        return thumbs;
    }

    public void setThumbs(String thumbs) {
        this.thumbs = thumbs;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotolist() {
        return photolist;
    }

    public void setPhotolist(String photolist) {
        this.photolist = photolist;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
}
