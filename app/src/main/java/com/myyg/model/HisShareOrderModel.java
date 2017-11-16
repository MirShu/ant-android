package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/8/2.
 */
public class HisShareOrderModel implements Serializable {
    private static final long serialVersionUID = -5027902166737085810L;

    private int sd_id;

    private String sd_title;

    private String sd_photolist;

    private String sd_content;

    private long sd_time;

    public int getSd_id() {
        return sd_id;
    }

    public void setSd_id(int sd_id) {
        this.sd_id = sd_id;
    }

    public String getSd_title() {
        return sd_title;
    }

    public void setSd_title(String sd_title) {
        this.sd_title = sd_title;
    }

    public String getSd_photolist() {
        return sd_photolist;
    }

    public void setSd_photolist(String sd_photolist) {
        this.sd_photolist = sd_photolist;
    }

    public String getSd_content() {
        return sd_content;
    }

    public void setSd_content(String sd_content) {
        this.sd_content = sd_content;
    }

    public long getSd_time() {
        return sd_time;
    }

    public void setSd_time(long sd_time) {
        this.sd_time = sd_time;
    }
}
