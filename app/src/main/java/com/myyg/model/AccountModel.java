package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/10/10.
 */

public class AccountModel implements Serializable {
    private static final long serialVersionUID = -1399792254444233355L;

    private float money;

    private float buy;

    private float recharge;

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public float getBuy() {
        return buy;
    }

    public void setBuy(float buy) {
        this.buy = buy;
    }

    public float getRecharge() {
        return recharge;
    }

    public void setRecharge(float recharge) {
        this.recharge = recharge;
    }
}
