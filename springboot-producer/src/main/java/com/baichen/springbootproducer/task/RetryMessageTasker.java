package com.baichen.springbootproducer.task;

import com.baichen.pojo.BrokerMessageLog;
import com.baichen.pojo.Order;
import com.baichen.springbootproducer.constant.Constants;
import com.baichen.springbootproducer.mapper.BrokerMessageLogMapper;
import com.baichen.springbootproducer.producer.RabbitOrderSender;
import com.baichen.springbootproducer.utils.FastJsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 消息补偿，即通过定时任务获取超时消息，重新发送
 */

@Component
public class RetryMessageTasker {


    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    @Scheduled(initialDelay = 5000, fixedDelay = 10000) //启动5秒后，10秒扫一次
    public void reSend() {
        System.out.println("-----------定时任务开始-----------");
        //pull status = 0 and timeout message
        List<BrokerMessageLog> list = brokerMessageLogMapper.query4StatusAndTimeoutMessage();
        list.forEach(messageLog -> {
            if (messageLog.getTryCount() >= 3) { // 超过3次则设置为消息发送失败
                //update fail message
                brokerMessageLogMapper.changeBrokerMessageLogStatus(messageLog.getMessageId(), Constants.ORDER_SEND_FAILURE, new Date());
            } else {
                // resend
                brokerMessageLogMapper.update4ReSend(messageLog.getMessageId(), new Date());
                // 重新发送的订单信息,需要将消息转为实体对象
                Order reSendOrder = FastJsonConvertUtil.convertJSONToObject(messageLog.getMessage(), Order.class);
                try {
                    rabbitOrderSender.sendOrder(reSendOrder);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("-----------异常处理-----------");
                }
            }
        });
    }
}

