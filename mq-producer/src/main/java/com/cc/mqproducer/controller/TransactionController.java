package com.cc.mqproducer.controller;

import com.alibaba.fastjson.JSON;
import com.cc.mqproducer.model.OrderItem;
import com.cc.mqproducer.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController()
@Slf4j
@RequestMapping("/transaction")
public class TransactionController {

  @Autowired private TransactionMQProducer transactionMQProducer;

  @Autowired private OrderService orderService;

  @RequestMapping("/sendTransactionMQ")
  public String sendTransactionMQ() {
    try {
      OrderItem orderItem = orderService.getOrderItem(1);
      Message msg =
          new Message("transactionTopics1", "cc", "KEY", JSON.toJSONString(orderItem).getBytes());
      orderItem.setStatus(3);
      orderService.updateOrderItem(orderItem);
      SendResult sendResult = transactionMQProducer.sendMessageInTransaction(msg, orderItem.getOrderId());
      System.out.printf("%s%n", sendResult);

      Thread.sleep(10);
    } catch (MQClientException | InterruptedException e) {
      e.printStackTrace();
    }
    return "success";
  }
}
