package com.cc.mqconcumer.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 1.测试消费多个topic的使用
 * 2.测试确保消息不丢的客户端写法
 */
@Slf4j
@Component
public class MessageListenerHandler implements MessageListenerConcurrently {

    @Value("${rocketmq.consumer.topic}")
    private String TOPIC = "cctest3";


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                    ConsumeConcurrentlyContext context) {
        if (CollectionUtils.isEmpty(msgs)) {
            log.info("receive blank msgs...");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = msgs.get(0);
        String msg = new String(messageExt.getBody());
        if (messageExt.getTopic().equals(TOPIC)) {
            // mock 消费逻辑
            mockConsume(msg);
        }
        //如何确保消息可靠？RocketMQ保证，我们只需要正确使用就行了，发生异常时返回重试状态，中间件会自动重试，默认重试16次
//        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    private void mockConsume(String msg){
        log.info("receive msg: {}.", msg);
    }
}
