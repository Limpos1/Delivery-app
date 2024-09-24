package com.sparta.delivery.menu.dto;

import lombok.Getter;

@Getter
public class MenuUpdateRequestDto {

    private Long menuId; //수정할 메뉴 아이디
    private String name; //수정할 메뉴 이름
    private int price; //수정할 메뉴 가격
    private Long restaurantId; //레스토랑 id

    public MenuUpdateRequestDto(Long menuId, String status, int price, Long restaurantId) {

        this.menuId = menuId;
        this.name = status;
        this.price = price;
        this.restaurantId = restaurantId;
    }
}
