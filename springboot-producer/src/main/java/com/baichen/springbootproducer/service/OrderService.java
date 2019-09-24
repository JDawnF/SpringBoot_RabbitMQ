package com.baichen.springbootproducer.service;

import com.baichen.pojo.BrokerMessageLog;
import com.baichen.pojo.Order;
import com.baichen.springbootproducer.constant.Constants;
import com.baichen.springbootproducer.mapper.BrokerMessageLogMapper;
import com.baichen.springbootproducer.mapper.OrderMapper;
import com.baichen.springbootproducer.producer.RabbitOrderSender;
import com.baichen.springbootproducer.utils.DateUtils;
import com.baichen.springbootproducer.utils.FastJsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    public void createOrder(Order order) throws Exception {
        // 使用当前时间当做订单创建时间（为了模拟一下简化）
        Date orderTime = new Date();
        // 插入业务数据，业务入库
        orderMapper.insert(order);
        // 插入消息记录表数据
        BrokerMessageLog brokerMessageLog = new BrokerMessageLog();
        // 消息唯一ID
        brokerMessageLog.setMessageId(order.getMessageId());
        // 保存消息整体 转为JSON 格式存储入库，将order转为json
        brokerMessageLog.setMessage(FastJsonConvertUtil.convertObjectToJSON(order));
        // 设置消息状态为0 表示消息发送中
        brokerMessageLog.setStatus("0");
        // 设置消息未确认超时时间窗口为 一分钟
        brokerMessageLog.setNextRetry(DateUtils.addMinutes(orderTime, Constants.ORDER_TIMEOUT));
        brokerMessageLog.setCreateTime(new Date());
        brokerMessageLog.setUpdateTime(new Date());
        //业务入库
        brokerMessageLogMapper.insertSelective(brokerMessageLog);
        // 发送消息
        rabbitOrderSender.sendOrder(order);
    }

}

