package com.myyg.wxapi.model;

import java.io.Serializable;

/**
 * Created by shiyuankao on 2016/8/11.
 */
public class WxBaseModel implements Serializable {
    private static final long serialVersionUID = -3440839491654871881L;

    private int errcode;

    private String errmsg;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
