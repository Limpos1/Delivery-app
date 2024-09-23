package com.sparta.delivery.order.dto;


import com.sparta.delivery.orders.enums.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class OrderResponseDto {
    Long userId;
    Long orderId;
    String address;
    String name;
    LocalDateTime orderTime;
    OrderStatus status;
    public OrderResponseDto(Long userId, String address, String name, LocalDateTime orderTime, OrderStatus status) {
        this.userId = userId;
        this.address = address;
        this.name = name;
        this.orderTime = orderTime;
        this.status = status;
    }
    public OrderResponseDto() {}
}
