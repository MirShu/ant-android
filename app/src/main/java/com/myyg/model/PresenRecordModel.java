package com.myyg.model;

import java.io.Serializable;

/**
 * Created by  on 2016/6/28.
 */
public class PresenRecordModel implements Serializable {
    private static final long serialVersionUID = 7907004182779135023L;
    private long time;
    private String money;

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
