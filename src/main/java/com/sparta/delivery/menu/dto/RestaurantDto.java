package com.sparta.delivery.menu.dto;

import lombok.Getter;

@Getter
public class RestaurantDto {
    private Long storeId; //식당아이디

    //생성자
    public RestaurantDto (Long storeId) {
        this.storeId = storeId;
    }

}
