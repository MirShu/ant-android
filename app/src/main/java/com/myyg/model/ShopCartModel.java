package com.myyg.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/6/26.
 */
@Table(name = "T_ShopCart")
public class ShopCartModel implements Serializable {
    private static final long serialVersionUID = 1225082018716893870L;

    @Id
    @NoAutoIncrement
    @Column(column = "ShopId")
    private String shopId;

    @Column(column = "GoodsId")
    private String goodsId;

    @Column(column = "Periods")
    private String periods;

    @Column(column = "GoodsName")
    private String goodsName;

    @Column(column = "GoodsImage")
    private String goodsImage;

    @Column(column = "TotalNumber")
    private int totalNumber;

    @Column(column = "SurplusNumber")
    private int surplusNumber;

    @Column(column = "JoinNumber")
    private int joinNumber;

    @Column(column = "Price")
    private double price;

    @Column(column = "TotalMoney")
    private double totalMoney;

    @Column(column = "CreateTime")
    private String createTime;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public int getSurplusNumber() {
        return surplusNumber;
    }

    public void setSurplusNumber(int surplusNumber) {
        this.surplusNumber = surplusNumber;
    }

    public int getJoinNumber() {
        return joinNumber;
    }

    public void setJoinNumber(int joinNumber) {
        this.joinNumber = joinNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
