package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/6/5.
 */
public class JoinModel implements Serializable {
    private static final long serialVersionUID = -8067283275640462993L;

    private int id;

    private String title;

    private String thumb;

    private String qishu;

    private int zongrenshu;

    private int canyurenshu;

    private int shenyurenshu;

    private String win_code;

    private String win_time;

    private String q_uid;

    private String q_user;

    private int q_number;

    private int number;

    private int win;

    private int process;

    private int append;

    private int next_id;

    private float yunjiage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getQishu() {
        return qishu;
    }

    public void setQishu(String qishu) {
        this.qishu = qishu;
    }

    public int getZongrenshu() {
        return zongrenshu;
    }

    public void setZongrenshu(int zongrenshu) {
        this.zongrenshu = zongrenshu;
    }

    public int getCanyurenshu() {
        return canyurenshu;
    }

    public void setCanyurenshu(int canyurenshu) {
        this.canyurenshu = canyurenshu;
    }

    public int getShenyurenshu() {
        return shenyurenshu;
    }

    public void setShenyurenshu(int shenyurenshu) {
        this.shenyurenshu = shenyurenshu;
    }

    public String getWin_code() {
        return win_code;
    }

    public void setWin_code(String win_code) {
        this.win_code = win_code;
    }

    public String getWin_time() {
        return win_time;
    }

    public void setWin_time(String win_time) {
        this.win_time = win_time;
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

    public int getQ_number() {
        return q_number;
    }

    public void setQ_number(int q_number) {
        this.q_number = q_number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public int getAppend() {
        return append;
    }

    public void setAppend(int append) {
        this.append = append;
    }

    public int getNext_id() {
        return next_id;
    }

    public void setNext_id(int next_id) {
        this.next_id = next_id;
    }

    public float getYunjiage() {
        return yunjiage;
    }

    public void setYunjiage(float yunjiage) {
        this.yunjiage = yunjiage;
    }
}
