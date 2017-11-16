package com.myyg.model;

import java.io.Serializable;

/**
 * Created by shiyuankao on 2016/9/19.
 */
public class WinningMessageModel implements Serializable {
    private static final long serialVersionUID = 606439984741133628L;

    private String id;

    private String title;

    private String qishu;

    private String q_user_code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQishu() {
        return qishu;
    }

    public void setQishu(String qishu) {
        this.qishu = qishu;
    }

    public String getQ_user_code() {
        return q_user_code;
    }

    public void setQ_user_code(String q_user_code) {
        this.q_user_code = q_user_code;
    }
}
