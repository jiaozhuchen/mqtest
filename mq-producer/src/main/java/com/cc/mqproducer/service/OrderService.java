package com.cc.mqproducer.service;

import com.cc.mqproducer.dao.OrderItemMapper;
import com.cc.mqproducer.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    public OrderItem getOrderItem(Integer orderId) {
        return orderItemMapper.selectByPrimaryKey(orderId);
    }

    public void updateOrderItem(OrderItem orderItem) {
        orderItemMapper.updateByPrimaryKey(orderItem);
    }
}
