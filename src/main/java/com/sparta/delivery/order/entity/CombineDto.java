package com.sparta.delivery.order.entity;

import com.sparta.delivery.order.dto.OrderDetailDto;
import com.sparta.delivery.order.dto.OrderResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombineDto {
    private OrderResponseDto orderResponseDto;
    private OrderDetailDto orderDetailDto;

    public CombineDto(OrderResponseDto orderResponseDto, OrderDetailDto orderDetailDto) {
        this.orderResponseDto = orderResponseDto;
        this.orderDetailDto = orderDetailDto;
    }
}
