package com.myyg.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/28.
 */
public class RechargeRecordModel implements Serializable {
    private static final long serialVersionUID = -4470547807516262746L;
    private String money;

    private String time;

    private String pay_type;

    private String status;

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
