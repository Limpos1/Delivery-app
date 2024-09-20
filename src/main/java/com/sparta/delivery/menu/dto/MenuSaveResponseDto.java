package com.sparta.delivery.menu.dto;

import lombok.Getter;

@Getter
public class MenuSaveResponseDto {


    private final String name;
    private final int price;
    private final RestaurantDto restaurantDto;
    private final Long menuId; //메뉴아이디

    public MenuSaveResponseDto(String name, int price, RestaurantDto restaurantDto, Long menuId) {

        this.name = name;
        this.price = price;
        this.restaurantDto = restaurantDto;
        this.menuId = menuId;
    }
}
