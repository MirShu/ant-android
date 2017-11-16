package com.myyg.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public class GoodsPastModel implements Serializable {
    private static final long serialVersionUID = 4449471530663871787L;

    private int qishu;

    private String q_end_time;

    private int q_uid;

    private long q_user_code;

    private long gonumber;

    private String member;

    private String ip;

    public int getQishu() {
        return qishu;
    }

    public void setQishu(int qishu) {
        this.qishu = qishu;
    }

    public String getQ_end_time() {
        return q_end_time;
    }

    public void setQ_end_time(String q_end_time) {
        this.q_end_time = q_end_time;
    }

    public int getQ_uid() {
        return q_uid;
    }

    public void setQ_uid(int q_uid) {
        this.q_uid = q_uid;
    }

    public long getQ_user_code() {
        return q_user_code;
    }

    public void setQ_user_code(long q_user_code) {
        this.q_user_code = q_user_code;
    }

    public long getGonumber() {
        return gonumber;
    }

    public void setGonumber(long gonumber) {
        this.gonumber = gonumber;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

}
