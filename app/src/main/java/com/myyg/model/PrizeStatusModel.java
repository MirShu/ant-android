package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/7/23.
 */
public class PrizeStatusModel implements Serializable {
    private static final long serialVersionUID = 5749856673660915305L;

    private String title;

    private String time;

    private boolean isSelected;

    public PrizeStatusModel() {
    }

    public PrizeStatusModel(String title, String time, boolean isSelected) {
        this.title = title;
        this.time = time;
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
