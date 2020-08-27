package com.cc.mqproducer.dao;

import com.cc.mqproducer.model.OrderItem;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

}
