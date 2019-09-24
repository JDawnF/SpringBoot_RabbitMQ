package com.baichen.springbootproducer.constant;

/**
 * 消息状态值
 */
public class Constants {
    public static final String ORDER_SENDING = "0"; //消息发送中

    public static final String ORDER_SEND_SUCCESS = "1"; //消息发送成功

    public static final String ORDER_SEND_FAILURE = "2"; //消息发送失败

    public static final int ORDER_TIMEOUT = 1; // 超时单位：min
}
