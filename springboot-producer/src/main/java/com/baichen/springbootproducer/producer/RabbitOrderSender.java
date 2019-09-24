package com.baichen.springbootproducer.producer;

import com.baichen.pojo.Order;
import com.baichen.springbootproducer.constant.Constants;
import com.baichen.springbootproducer.mapper.BrokerMessageLogMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 发送订单消息
 */


@Component
public class RabbitOrderSender {
    //自动注入RabbitTemplate模板类
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    //回调函数: confirm确认
    final ConfirmCallback confirmCallback = new ConfirmCallback() {
        /**
         * @param correlationData 消息唯一ID
         * @param ack 消息发送状态
         * @param cause 原因
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.err.println("correlationData: " + correlationData);
            String messageId = correlationData.getId();
            if (ack) { // 发送成功
                //如果confirm返回成功 则进行更新消息日志表状态
                brokerMessageLogMapper.changeBrokerMessageLogStatus(messageId, Constants.ORDER_SEND_SUCCESS, new Date());
            } else {
                //失败则进行具体的后续操作:重试 或者补偿等手段
                System.err.println("异常处理...");
            }
        }
    };

    //发送消息方法调用: 构建自定义对象消息
    public void sendOrder(Order order) throws Exception {
        // 通过实现 ConfirmCallback 接口，消息发送到 Broker 后触发回调，确认消息是否到达 Broker 服务器，也就是只确认是否正确到达 Exchange 中
        rabbitTemplate.setConfirmCallback(confirmCallback);
        //消息唯一ID
        CorrelationData correlationData = new CorrelationData(order.getMessageId());
        rabbitTemplate.convertAndSend("order-exchange", "order.ABC", order, correlationData);
    }
}
