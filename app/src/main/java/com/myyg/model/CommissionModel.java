package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/10/7.
 */

public class CommissionModel implements Serializable {
    private static final long serialVersionUID = 3013918429727752507L;

    private String name;

    private String ygmoney;

    private String money;

    private long time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYgmoney() {
        return ygmoney;
    }

    public void setYgmoney(String ygmoney) {
        this.ygmoney = ygmoney;
    }

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
