package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/6/4.
 */
public class GoodsModel extends CountdownBaseModel implements Serializable {
    private static final long serialVersionUID = -7841487143372928691L;

    private String status_desc;

    private String title;

    private String thumb;

    private int zongrenshu;

    private int shenyurenshu;

    private int canyurenshu;

    private String qishu;

    private long time;

    private String xsjx_time;

    private String q_uid;

    private String q_user;

    private String q_user_code;

    private String q_showtime;

    private long q_end_time;

    private int gonumber;

    private String picarr;

    private String win_time;

    private int status;

    private String win_code;

    private float money;

    private float yunjiage;

    private int process;

    private int win;

    public String getStatus_desc() {
        return status_desc;
    }

    public void setStatus_desc(String status_desc) {
        this.status_desc = status_desc;
    }

    public String getWin_time() {
        return win_time;
    }

    public void setWin_time(String win_time) {
        this.win_time = win_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getWin_code() {
        return win_code;
    }

    public void setWin_code(String win_code) {
        this.win_code = win_code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getZongrenshu() {
        return zongrenshu;
    }

    public String getQ_uid() {
        return q_uid;
    }

    public void setQ_uid(String q_uid) {
        this.q_uid = q_uid;
    }

    public String getQ_user() {
        return q_user;
    }

    public void setQ_user(String q_user) {
        this.q_user = q_user;
    }

    public String getQ_user_code() {
        return q_user_code;
    }

    public void setQ_user_code(String q_user_code) {
        this.q_user_code = q_user_code;
    }

    public String getQ_showtime() {
        return q_showtime;
    }

    public void setQ_showtime(String q_showtime) {
        this.q_showtime = q_showtime;
    }

    public int getGonumber() {
        return gonumber;
    }

    public void setGonumber(int gonumber) {
        this.gonumber = gonumber;
    }

    public void setZongrenshu(int zongrenshu) {
        this.zongrenshu = zongrenshu;
    }

    public int getShenyurenshu() {
        return shenyurenshu;
    }

    public void setShenyurenshu(int shenyurenshu) {
        this.shenyurenshu = shenyurenshu;
    }

    public int getCanyurenshu() {
        return canyurenshu;
    }

    public void setCanyurenshu(int canyurenshu) {
        this.canyurenshu = canyurenshu;
    }

    public String getQishu() {
        return qishu;
    }

    public void setQishu(String qishu) {
        this.qishu = qishu;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getXsjx_time() {
        return xsjx_time;
    }

    public void setXsjx_time(String xsjx_time) {
        this.xsjx_time = xsjx_time;
    }

    public long getQ_end_time() {
        return q_end_time;
    }

    public void setQ_end_time(long q_end_time) {
        this.q_end_time = q_end_time;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public float getYunjiage() {
        return yunjiage;
    }

    public void setYunjiage(float yunjiage) {
        this.yunjiage = yunjiage;
    }

    public String getPicarr() {
        return picarr;
    }

    public void setPicarr(String picarr) {
        this.picarr = picarr;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }
}
