package com.myyg.constant;

/**
 * Created by JOHN on 2016/4/3.
 */
public class SysEnums {
    public enum EnumJoinStatus {
        All(1),//所有参与记录
        Opened(2),//已揭晓记录
        Process(3);//进行中记录

        EnumJoinStatus(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    }

    /**
     * 是否晒单
     */
    public enum EnumBaskOrder {
        Yes(1),//已晒单
        No(2);//未晒单

        EnumBaskOrder(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    }

    /**
     * 商品状态
     */
    public enum EnumGoodsStatus {
        Ongoing(0),//进行中
        Announced(1),//已揭晓
        Announcing(2);//揭晓中

        EnumGoodsStatus(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    }

    /**
     * 消息推送业务类型
     */
    public enum EnumPushBusiness {
        Winning(1);// 中奖消息推送

        EnumPushBusiness(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    }

    /**
     * 第三方登录类型
     */
    public enum EnumAuthLogin {
        Wx(1),
        QQ(2);

        EnumAuthLogin(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    }

    /**
     * 支付渠道
     */
    public enum EnumPaymentChannel {

        WxPayment("weixin", "微信支付"),
        UnionPayment("unionpay", "银联支付");

        EnumPaymentChannel(String value, String text) {
            this.value = value;
            this.text = text;
        }

        private String value;

        private String text;

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * 订单支付方式
     */
    public enum EnumOrderPaymentMode {
        Score("fufen", "云购币支付"),
        Momey("money", "余额支付"),
        WxPayment("wx_payment", "微信支付");

        EnumOrderPaymentMode(String value, String text) {
            this.value = value;
            this.text = text;
        }

        private String value;

        private String text;

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * Yes/No
     */
    public enum EnumYesNo {
        No(0),
        Yes(1);

        EnumYesNo(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    }
}
