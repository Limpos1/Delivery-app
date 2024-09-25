package com.sparta.delivery.orders.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CombineDto {
    private OrderResponseDto orderResponseDto;
    private List<OrderDetailDto> orderDetailDto;

    public CombineDto(OrderResponseDto orderResponseDto, List<OrderDetailDto> orderDetailDto) {
        this.orderResponseDto = orderResponseDto;
        this.orderDetailDto = orderDetailDto;
    }
}
