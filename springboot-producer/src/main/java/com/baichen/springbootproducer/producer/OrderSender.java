package com.baichen.springbootproducer.producer;

import com.baichen.pojo.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderSender {
    // 发送消息的模板
    @Autowired private RabbitTemplate rabbitTemplate;

    // 发送消息
    public void send(Order order){
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(order.getMessageId());
        /**
         * exchange
         * routingKey
         * 消息体内容
         * CorrelationData：消息唯一ID
         */
        rabbitTemplate.convertAndSend("order-exchange","order.abcd",order,correlationData);
        /**
         * 还要在 rabbitmq 控制台配置exchange和queue，并绑定；加绑定在控制台的exchange和queues哪一块都可以
         */
    }
}
