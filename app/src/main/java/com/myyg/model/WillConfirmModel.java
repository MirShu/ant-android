package com.myyg.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/23.
 */
public class WillConfirmModel implements Serializable {
    private static final long serialVersionUID = -921615701056039087L;
    private String title;
    private String thumb;
    private String qishu;
    private String zongrenshu;
    private String gonumber;
    private String win_code;
    private String q_end_time;
    private String address_time;
    private String send_time;
    private String receive_time;
    private String company;
    private String company_code;
    private AddressModel address;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getQishu() {
        return qishu;
    }

    public void setQishu(String qishu) {
        this.qishu = qishu;
    }

    public String getZongrenshu() {
        return zongrenshu;
    }

    public void setZongrenshu(String zongrenshu) {
        this.zongrenshu = zongrenshu;
    }

    public String getGonumber() {
        return gonumber;
    }

    public void setGonumber(String gonumber) {
        this.gonumber = gonumber;
    }

    public String getWin_code() {
        return win_code;
    }

    public void setWin_code(String win_code) {
        this.win_code = win_code;
    }

    public String getQ_end_time() {
        return q_end_time;
    }

    public void setQ_end_time(String q_end_time) {
        this.q_end_time = q_end_time;
    }

    public String getAddress_time() {
        return address_time;
    }

    public void setAddress_time(String address_time) {
        this.address_time = address_time;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(String receive_time) {
        this.receive_time = receive_time;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany_code() {
        return company_code;
    }

    public void setCompany_code(String company_code) {
        this.company_code = company_code;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }
}
