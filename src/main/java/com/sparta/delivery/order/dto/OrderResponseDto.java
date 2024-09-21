package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.enums.OrderStatus;
import com.sparta.delivery.user.entity.User;
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
