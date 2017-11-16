package com.myyg.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JOHN on 2016/6/10.
 */
public class GoodsListModel implements Serializable {
    private static final long serialVersionUID = 8234678043403114200L;

    private int total;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    private String thumb;

    private List<GoodsModel> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GoodsModel> getList() {
        return list;
    }

    public void setList(List<GoodsModel> list) {
        this.list = list;
    }
}
