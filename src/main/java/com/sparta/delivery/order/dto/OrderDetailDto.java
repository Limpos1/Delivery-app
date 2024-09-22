package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.entity.Orders;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
public class OrderDetailDto {
    Long orderId;
    Long menuid;
    Long restaurantid;
    Long count;
    Long price;
    LocalDateTime ordertime;

    public OrderDetailDto(Long orderid, Long menuid, Long restaurantid, Long count, Long price, LocalDateTime ordertime) {
        this.orderId = orderid;
        this.menuid = menuid;
        this.restaurantid = restaurantid;
        this.count = count;
        this.price = price;
        this.ordertime = ordertime;
    }
}
