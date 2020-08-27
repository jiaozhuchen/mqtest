package com.cc.mqconcumer.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MessageOrderItemOrderlyListenerHandler implements MessageListenerOrderly {
    @Value("${rocketmq.consumer.topicOrderly}")
    private String TOPIC;

    private Random random = new Random();

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        context.setAutoCommit(true);
        for (MessageExt msg : msgs) {
            // 可以看到每个queue有唯一的consume线程来消费, 订单对每个queue(分区)有序
            System.out.println("consumeThread=" + Thread.currentThread().getName() + "queueId=" + msg.getQueueId() + ", content:" + new String(msg.getBody()));
        }

        try {
            //模拟业务逻辑处理中...
            TimeUnit.SECONDS.sleep(random.nextInt(10));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
