package com.sparta.delivery.menu.dto;

import lombok.Getter;

@Getter
public class MenuUpdateResponseDto {
    private final Long menuId;
    private final String name;
    private final int price;
    private final RestaurantDto restaurantDto;

    public MenuUpdateResponseDto(Long menuId, String name, int price, RestaurantDto restaurantDto) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.restaurantDto = restaurantDto;
    }
}
