package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.entity.Orders;
import lombok.Getter;

@Getter
public class OrderDetailDto {
    Long orderId;
    Long menuid;
    Long restaurantid;
    Long count;
    Long price;

    public OrderDetailDto(Long orderid, Long menuid, Long restaurantid, Long count, Long price) {
        this.orderId = orderid;
        this.menuid = menuid;
        this.restaurantid = restaurantid;
        this.count = count;
        this.price = price;
    }
}
