package com.cc.mqproducer.model;

import lombok.Data;

import java.util.Date;

@Data
public class OrderItem {
    private Integer orderId;

    private String orderNo;

    private Date orderTime;

    private String skuList;

    private String desc;

    private Integer status;
}
