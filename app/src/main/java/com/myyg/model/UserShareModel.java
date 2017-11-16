package com.myyg.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JOHN on 2016/7/16.
 */
public class UserShareModel implements Serializable {
    private static final long serialVersionUID = 6426646240256581217L;

    private List<ShareOrderModel> lists;

    private int count;

    public List<ShareOrderModel> getLists() {
        return lists;
    }

    public void setLists(List<ShareOrderModel> lists) {
        this.lists = lists;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public class ShareOrderModel implements Serializable {
        private static final long serialVersionUID = 2894814590375036550L;

        private String title;

        private int sd_id;

        private String sd_title;

        private String sd_thumbs;

        private String sd_content;

        private long sd_time;

        /**
         * 揭晓时间
         */
        private long q_end_time;

        /**
         * 中奖号码
         */
        private String q_user_code;

        /**
         * 中奖ID
         */
        private String rid;

        /**
         *
         */
        private int shopid;

        /**
         *
         */
        private String thumb;

        private int sd_status;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSd_id() {
            return sd_id;
        }

        public void setSd_id(int sd_id) {
            this.sd_id = sd_id;
        }

        public String getSd_title() {
            return sd_title;
        }

        public void setSd_title(String sd_title) {
            this.sd_title = sd_title;
        }

        public String getSd_thumbs() {
            return sd_thumbs;
        }

        public void setSd_thumbs(String sd_thumbs) {
            this.sd_thumbs = sd_thumbs;
        }

        public String getSd_content() {
            return sd_content;
        }

        public void setSd_content(String sd_content) {
            this.sd_content = sd_content;
        }

        public long getSd_time() {
            return sd_time;
        }

        public void setSd_time(long sd_time) {
            this.sd_time = sd_time;
        }

        public long getQ_end_time() {
            return q_end_time;
        }

        public void setQ_end_time(long q_end_time) {
            this.q_end_time = q_end_time;
        }

        public String getQ_user_code() {
            return q_user_code;
        }

        public void setQ_user_code(String q_user_code) {
            this.q_user_code = q_user_code;
        }

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public int getShopid() {
            return shopid;
        }

        public void setShopid(int shopid) {
            this.shopid = shopid;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public int getSd_status() {
            return sd_status;
        }

        public void setSd_status(int sd_status) {
            this.sd_status = sd_status;
        }
    }
}
