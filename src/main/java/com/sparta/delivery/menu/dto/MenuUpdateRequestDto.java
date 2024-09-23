package com.sparta.delivery.menu.dto;

import lombok.Getter;

@Getter
public class MenuUpdateRequestDto {
    private Long userId; //사장님Id
    private Long menuId; //수정할 메뉴 아이디
    private String name; //수정할 메뉴 이름
    private int price; //수정할 메뉴 가격
    private RestaurantDto restaurantDto; //레스토랑 id

    public MenuUpdateRequestDto(Long userId, Long menuId, String status, int price, RestaurantDto restaurantDto) {
        this.userId = userId;
        this.menuId = menuId;
        this.name = status;
        this.price = price;
        this.restaurantDto = restaurantDto;
    }
}
