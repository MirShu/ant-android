package com.myyg.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/7/2.
 */
@Table(name = "T_Area")
public class AreaModel implements Serializable {
    private static final long serialVersionUID = -6107330805071240096L;

    @Id
    @NoAutoIncrement
    @Column(column = "AreaId")
    private int areaId;

    @Column(column = "AreaName")
    private String areaName;

    @Column(column = "ParentId")
    private int parentId;

    @Column(column = "FullName")
    private String fullName;

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
