package com.myyg.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
public class ShowSearchGoodsModel implements Serializable {
    private static final long serialVersionUID = 4870131305392727607L;
    private List<ShowGoodsModel> lists;

    private int count;

    private int total;

    public List<ShowGoodsModel> getLists() {
        return lists;
    }

    public void setLists(List<ShowGoodsModel> lists) {
        this.lists = lists;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public class ShowGoodsModel implements Serializable {


        /**
         * id : 325
         * title : 宏碁 Acer F5-572G-54ZZ 15.6英寸笔记本 i5-6200U 4G 1T 940M 2G独显
         * thumb : shopimg/20160601/24235046784483.jpg
         * qishu : 14
         * zongrenshu : 4998
         * canyurenshu : 7
         * shenyurenshu : 4991
         * money : 4998.00
         * yunjiage : 1.00
         */

        private int id;
        private String title;
        private String thumb;
        private int qishu;
        private String zongrenshu;
        private String canyurenshu;
        private String shenyurenshu;
        private String money;
        private String yunjiage;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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

        public int getQishu() {
            return qishu;
        }

        public void setQishu(int qishu) {
            this.qishu = qishu;
        }

        public String getZongrenshu() {
            return zongrenshu;
        }

        public void setZongrenshu(String zongrenshu) {
            this.zongrenshu = zongrenshu;
        }

        public String getCanyurenshu() {
            return canyurenshu;
        }

        public void setCanyurenshu(String canyurenshu) {
            this.canyurenshu = canyurenshu;
        }

        public String getShenyurenshu() {
            return shenyurenshu;
        }

        public void setShenyurenshu(String shenyurenshu) {
            this.shenyurenshu = shenyurenshu;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getYunjiage() {
            return yunjiage;
        }

        public void setYunjiage(String yunjiage) {
            this.yunjiage = yunjiage;
        }
    }
}
