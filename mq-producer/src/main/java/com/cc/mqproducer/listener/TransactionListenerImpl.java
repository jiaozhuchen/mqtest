package com.cc.mqproducer.listener;

import com.alibaba.fastjson.JSON;
import com.cc.mqproducer.model.OrderItem;
import com.cc.mqproducer.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TransactionListenerImpl implements TransactionListener {

    @Autowired
    private OrderService orderService;

    private ConcurrentHashMap<String, LocalTransactionState> stateMap = new ConcurrentHashMap<>();
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        log.info("message is : {}", msg);
        log.info("arg is : {}", arg);
        Integer orderId = (Integer) arg;
        OrderItem orderItem = orderService.getOrderItem(orderId);
        Integer status = orderItem.getStatus();
        if(status == null || status == 0) {
            stateMap.put(msg.getTransactionId(), LocalTransactionState.UNKNOW);
        }else if(status >0 && status<=3) {
            stateMap.put(msg.getTransactionId(), LocalTransactionState.COMMIT_MESSAGE);
        }else {
            stateMap.put(msg.getTransactionId(), LocalTransactionState.ROLLBACK_MESSAGE);
        }
        log.info("executeLocalTransaction executed");
        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        log.info("msg is : {}", msg);
        String msgs = new String(msg.getBody());


        OrderItem orderItem = JSON.parseObject(new String(msg.getBody()), OrderItem.class);
        if(orderItem != null) {
            orderItem = orderService.getOrderItem(orderItem.getOrderId());
            Integer status = orderItem.getStatus();
            if(status == null || status == 0) {
                stateMap.put(msg.getTransactionId(), LocalTransactionState.UNKNOW);
            }else if(status >0 && status<=3) {
                stateMap.put(msg.getTransactionId(), LocalTransactionState.COMMIT_MESSAGE);
            }else {
                stateMap.put(msg.getTransactionId(), LocalTransactionState.ROLLBACK_MESSAGE);
            }
        }
        return stateMap.get(msg.getTransactionId());
    }
}
