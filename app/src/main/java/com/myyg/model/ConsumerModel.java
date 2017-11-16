package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/10/10.
 */

public class ConsumerModel implements Serializable {
    private static final long serialVersionUID = -6798364477434727734L;

    private float money;

    private long time;

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
