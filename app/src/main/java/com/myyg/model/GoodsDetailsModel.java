package com.myyg.model;

import java.io.Serializable;

/**
 * Created by JOHN on 2016/7/2.
 */
public class GoodsDetailsModel extends GoodsModel implements Serializable {
    private static final long serialVersionUID = -2318020582374668720L;
    private int sid;

    private String title2;

    private String describe;

    private String next_qishu;

    private int next_id;

    private User user;

    private Current current;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getNext_qishu() {
        return next_qishu;
    }

    public void setNext_qishu(String next_qishu) {
        this.next_qishu = next_qishu;
    }

    public int getNext_id() {
        return next_id;
    }

    public void setNext_id(int next_id) {
        this.next_id = next_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Current getCurrent() {
        if (this.current == null) {
            this.current = new Current();
        }
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public class User {
        private String q_uid;

        private String q_name;

        private String q_img;

        private String q_user_code;

        private long q_end_time;

        private String q_show_time;

        private int gonumber;

        private long countdown;

        public String getQ_uid() {
            return q_uid;
        }

        public void setQ_uid(String q_uid) {
            this.q_uid = q_uid;
        }

        public String getQ_name() {
            return q_name;
        }

        public void setQ_name(String q_name) {
            this.q_name = q_name;
        }

        public String getQ_img() {
            return q_img;
        }

        public void setQ_img(String q_img) {
            this.q_img = q_img;
        }

        public String getQ_user_code() {
            return q_user_code;
        }

        public void setQ_user_code(String q_user_code) {
            this.q_user_code = q_user_code;
        }

        public long getQ_end_time() {
            return q_end_time;
        }

        public void setQ_end_time(long q_end_time) {
            this.q_end_time = q_end_time;
        }

        public String getQ_show_time() {
            return q_show_time;
        }

        public void setQ_show_time(String q_show_time) {
            this.q_show_time = q_show_time;
        }

        public int getGonumber() {
            return gonumber;
        }

        public void setGonumber(int gonumber) {
            this.gonumber = gonumber;
        }

        public long getCountdown() {
            return countdown;
        }

        public void setCountdown(long countdown) {
            this.countdown = countdown;
        }
    }

    public class Current {
        private int gonumber;

        private String goucode;

        public int getGonumber() {
            return gonumber;
        }

        public void setGonumber(int gonumber) {
            this.gonumber = gonumber;
        }

        public String getGoucode() {
            return goucode;
        }

        public void setGoucode(String goucode) {
            this.goucode = goucode;
        }
    }
}
