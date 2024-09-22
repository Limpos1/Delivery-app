package com.sparta.delivery.menu.dto;

import lombok.Getter;

@Getter
public class MenuDeleteRequestDto {
    private Long userId; //사장님 ID확인
    private RestaurantDto restaurant;

    public MenuDeleteRequestDto(Long userId, RestaurantDto restaurantDto) {
        this.userId = userId;
        this.restaurant = restaurantDto;
    }
}
