package com.myyg.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JOHN on 2016/7/4.
 */
public class LastLotteryModel implements Serializable {
    private static final long serialVersionUID = -416438424711338588L;
    private LatestModel latest;
    private List<PrevModel> prev;

    public LatestModel getLatest() {
        return latest;
    }

    public void setLatest(LatestModel latest) {
        this.latest = latest;
    }

    public List<PrevModel> getPrev() {
        return prev;
    }

    public void setPrev(List<PrevModel> prev) {
        this.prev = prev;
    }

    public class PrevModel implements Serializable {
        private static final long serialVersionUID = 7251768072919670767L;
        private int id;
        private String qishu;
        private long q_end_time;
        private String q_uid;
        private String q_user_code;
        private String ip;
        private String img;
        private String q_user;
        private String gonumber;
        private String member;
        private String q_show_uid;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getQ_user() {
            return q_user;
        }

        public void setQ_user(String q_user) {
            this.q_user = q_user;
        }

        public String getQishu() {
            return qishu;
        }

        public void setQishu(String qishu) {
            this.qishu = qishu;
        }

        public long getQ_end_time() {
            return q_end_time;
        }

        public void setQ_end_time(long q_end_time) {
            this.q_end_time = q_end_time;
        }

        public String getQ_uid() {
            return q_uid;
        }

        public void setQ_uid(String q_uid) {
            this.q_uid = q_uid;
        }

        public String getQ_user_code() {
            return q_user_code;
        }

        public void setQ_user_code(String q_user_code) {
            this.q_user_code = q_user_code;
        }

        public String getGonumber() {
            return gonumber;
        }

        public void setGonumber(String gonumber) {
            this.gonumber = gonumber;
        }

        public String getMember() {
            return member;
        }

        public void setMember(String member) {
            this.member = member;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getQ_show_uid() {
            return q_show_uid;
        }

        public void setQ_show_uid(String q_show_uid) {
            this.q_show_uid = q_show_uid;
        }
    }

    public class LatestModel implements Serializable {
        private static final long serialVersionUID = 122891693078097534L;
        private String qishu;

        private int next_id;

        private String showtime;

        public String getQishu() {
            return qishu;
        }

        public void setQishu(String qishu) {
            this.qishu = qishu;
        }

        public int getNext_id() {
            return next_id;
        }

        public void setNext_id(int next_id) {
            this.next_id = next_id;
        }

        public String getShowtime() {
            return showtime;
        }

        public void setShowtime(String showtime) {
            this.showtime = showtime;
        }
    }
}
