package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/7/2.
 */
public class ShopRecordsModel implements Serializable {
    private static final long serialVersionUID = 6072106545565016847L;

    private String id;

    private String uid;

    private String username;

    private String uphoto;

    private String goucode;

    private String ip;

    private int gonumber;

    private long time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUphoto() {
        return uphoto;
    }

    public void setUphoto(String uphoto) {
        this.uphoto = uphoto;
    }

    public String getGoucode() {
        return goucode;
    }

    public void setGoucode(String goucode) {
        this.goucode = goucode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getGonumber() {
        return gonumber;
    }

    public void setGonumber(int gonumber) {
        this.gonumber = gonumber;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
