package com.sparta.delivery.menu.dto;

import lombok.Getter;

@Getter
public class MenuSaveRequestDto {
    private Long userId;//사용자 ID(사장님 Id)
    private String name; //메뉴이름
    private int price; //메뉴가격
    private RestaurantDto restaurantDto; //식당이름
}
