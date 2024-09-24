package com.sparta.delivery.menu.dto;

import lombok.Getter;

@Getter
public class MenuSaveRequestDto {
    private String name; //메뉴이름
    private int price; //메뉴가격
    private Long restaurantId; //식당아이디


    public MenuSaveRequestDto(String menu, int price, long restaurantId) {
        this.name = menu;
        this.price = price;
        this.restaurantId = restaurantId;
    }
}

