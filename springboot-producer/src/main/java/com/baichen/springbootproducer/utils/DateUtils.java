package com.baichen.springbootproducer.utils;

import java.util.Date;

public class DateUtils {
    public static Date addMinutes(Date orderTime, int orderTimeout) {
        Date afterDate = new Date(orderTime.getTime() + 60000 * orderTimeout);// 超时时间为一分钟
        return afterDate;
    }
}
