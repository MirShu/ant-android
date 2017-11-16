package com.myyg.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16.
 */
public class AddressModel implements Serializable {
    private static final long serialVersionUID = 7976875960789452279L;
    private String id;

    private String mobile;

    private String sheng;

    private String jiedao;

    private String shi;

    private String xian;

    private String youbian;

    private String shouhuoren;

    private String tell;

    private String isdefault;

    public List<AddressModel> getList() {
        return list;
    }

    public void setList(List<AddressModel> list) {
        this.list = list;
    }

    private List<AddressModel> list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSheng() {
        return sheng;
    }

    public void setSheng(String sheng) {
        this.sheng = sheng;
    }

    public String getShi() {
        return shi;
    }

    public void setShi(String shi) {
        this.shi = shi;
    }

    public String getXian() {
        return xian;
    }

    public void setXian(String xian) {
        this.xian = xian;
    }

    public String getYoubian() {
        return youbian;
    }

    public void setYoubian(String youbian) {
        this.youbian = youbian;
    }

    public String getShouhuoren() {
        return shouhuoren;
    }

    public void setShouhuoren(String shouhuoren) {
        this.shouhuoren = shouhuoren;
    }

    public String getTell() {
        return tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public boolean getIsdefault() {
        boolean flag = false;
        if (this.isdefault.equals("Y")) {
            flag = true;
        }
        return flag;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault;
    }

    public String getJiedao() {
        return jiedao;
    }

    public void setJiedao(String jiedao) {
        this.jiedao = jiedao;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
