package com.cc.mqproducer.controller;

import com.cc.mqproducer.model.OrderItem;
import com.cc.mqproducer.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController("order")
public class OrderItemController {

    @Autowired
    private OrderService orderService;

    private static String TOPIC = "cctest3";
    private static String TOPIC2 = "cctest2";
    private static String TAGS = "cc";

    @Autowired
    private DefaultMQProducer defaultMQProducer;


    @GetMapping("/getOrderById")
    public OrderItem getOrderItemById(@RequestParam Integer id) {
        return orderService.getOrderItem(id);
    }

    //配合测试消费者订阅多个消息实验
    @GetMapping("/sendOrderWithTopics")
    public OrderItem sendOrderWithTopics() throws InterruptedException, RemotingException, MQClientException, MQBrokerException, UnsupportedEncodingException {
        OrderItem orderItem = orderService.getOrderItem(1);
        Message msg = new Message(TOPIC, TAGS, orderItem.getOrderNo().getBytes(RemotingHelper.DEFAULT_CHARSET));
        // 调用客户端发送消息
        SendResult sendResult = defaultMQProducer.send(msg);
        log.info("sendOrder topic: {}", sendResult);
        Message msg2 = new Message(TOPIC2, TAGS, orderItem.toString().getBytes(RemotingHelper.DEFAULT_CHARSET));
        SendResult sendResult2 = defaultMQProducer.send(msg2);
        log.info("sendOrder topic2: {}", sendResult2);
        return orderItem;
    }

    @GetMapping("/sendOrderItemOrderly")
    public void sendOrderItemOrderly() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        List<OrderItem> orderItemList = buildOrders();
        for(int i=0; i<orderItemList.size(); i++) {
            String body = orderItemList.get(i).toString();
            Message msg = new Message("testMQOrderly", TAGS, "KEY" + i, body.getBytes());

            SendResult sendResult = defaultMQProducer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;  //根据订单id选择发送queue
                    long index = id % mqs.size();
                    return mqs.get((int) index);
                }
            }, orderItemList.get(i).getOrderId());//订单id

            log.info("SendResult status: {}, queueId: {}, body: {}",
                    sendResult.getSendStatus(),
                    sendResult.getMessageQueue().getQueueId(),
                    body);
        }
    }

    private List<OrderItem> buildOrders() {
        List<OrderItem> orderList = new ArrayList<OrderItem>();

        OrderItem orderDemo = new OrderItem();
        orderDemo.setOrderId(3111039);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(3111065);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(2);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(3111039);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(3117235);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(3111065);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);
        orderDemo = new OrderItem();
        orderDemo.setOrderId(2);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(3117235);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(3111065);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(2);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(3111039);
        orderDemo.setDesc("推送");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(3117235);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        orderDemo = new OrderItem();
        orderDemo.setOrderId(3111039);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        return orderList;
    }
}
