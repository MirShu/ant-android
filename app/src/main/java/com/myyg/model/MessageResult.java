package com.myyg.model;


import com.alibaba.fastjson.JSON;
import com.myyg.R;
import com.myyg.base.BaseApplication;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/4/3.
 */
public class MessageResult implements Serializable {
    private static final long serialVersionUID = 8125047647533402016L;
    private int code;

    private String msg;

    private String data;

    public MessageResult() {
        this.code = -1;
        this.msg = BaseApplication.getInstance().getResources().getString(R.string.data_exception);
    }

    public MessageResult(int code) {
        this.code = code;
        this.msg = BaseApplication.getInstance().getResources().getString(R.string.data_exception);
    }

    public MessageResult(int code, String message, String obj) {
        this.code = code;
        this.msg = message;
        this.data = obj;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * @param result
     * @return
     */
    public static MessageResult parse(String result) {
        MessageResult message = new MessageResult();
        try {
            message = JSON.parseObject(result, MessageResult.class);
            return message;
        } catch (Exception e) {
            String str = e.getMessage();
        }
        return message;
    }
}
