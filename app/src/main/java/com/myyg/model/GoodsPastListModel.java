package com.myyg.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public class GoodsPastListModel implements Serializable {
    private static final long serialVersionUID = 3519646896617512097L;
    private List<GoodsPastModel> list;

    private int qishu;

    private String img;

    public int getQishu() {
        return qishu;
    }

    public void setQishu(int qishu) {
        this.qishu = qishu;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public List<GoodsPastModel> getList() {
        return list;
    }

    public void setList(List<GoodsPastModel> list) {
        this.list = list;
    }
}
