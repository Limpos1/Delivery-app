package com.sparta.delivery.orders.dto;


import com.sparta.delivery.orders.enums.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class OrderResponseDto {
    Long userId;
    Long orderId;
    Long count;
    Long totalPrice;
    String address;
    String name;
    LocalDateTime orderTime;
    OrderStatus status;
    public OrderResponseDto(Long userId, String address, String name, LocalDateTime orderTime, OrderStatus status, Long orderId, Long count, Long totalPrice) {
        this.orderId = orderId;
        this.count = count;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.address = address;
        this.name = name;
        this.orderTime = orderTime;
        this.status = status;
    }
    public OrderResponseDto() {}
}
