package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/8/6.
 */
public class BizPushMessageModel implements Serializable {
    private static final long serialVersionUID = 7154329416974743118L;

    private int bizCode;

    private String displayMessage;

    private String data;

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
