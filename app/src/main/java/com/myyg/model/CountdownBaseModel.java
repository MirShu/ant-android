package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/6/23.
 */
public class CountdownBaseModel implements Serializable {
    private int id;
    private long countdown;
    private long endTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCountdown() {
        return countdown;
    }

    public void setCountdown(long countdown) {
        this.countdown = countdown;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
