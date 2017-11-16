package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/9/7.
 */
public class PaymentModel implements Serializable {
    private static final long serialVersionUID = 8191774116251395426L;

    private String orderid;

    private String pay_type;

    private String data;

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
