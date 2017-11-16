package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/7/15.
 */
public class CartItemModel implements Serializable {
    private static final long serialVersionUID = 24788444485359812L;

    private String id;

    private double yunjiage;

    private int zongrenshu;

    private int shenyurenshu;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getYunjiage() {
        return yunjiage;
    }

    public void setYunjiage(double yunjiage) {
        this.yunjiage = yunjiage;
    }

    public int getZongrenshu() {
        return zongrenshu;
    }

    public void setZongrenshu(int zongrenshu) {
        this.zongrenshu = zongrenshu;
    }

    public int getShenyurenshu() {
        return shenyurenshu;
    }

    public void setShenyurenshu(int shenyurenshu) {
        this.shenyurenshu = shenyurenshu;
    }
}
