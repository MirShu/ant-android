package com.myyg.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/29.
 */
public class FriendsModel implements Serializable {
    private static final long serialVersionUID = 8146526185591643407L;
    private long time;

    private String buy_status;

    private String name;

    public String getBuy_status() {
        return buy_status;
    }

    public void setBuy_status(String buy_status) {
        this.buy_status = buy_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}
