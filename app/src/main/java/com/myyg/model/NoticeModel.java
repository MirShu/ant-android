package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/8/3.
 */
public class NoticeModel implements Serializable {
    private static final long serialVersionUID = -7091145936408709994L;

    private int id;

    private String title;

    private long send_time;

    private int is_read;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSend_time() {
        return send_time;
    }

    public void setSend_time(long send_time) {
        this.send_time = send_time;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }
}
