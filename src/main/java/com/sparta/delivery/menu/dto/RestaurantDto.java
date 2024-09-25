package com.sparta.delivery.menu.dto;

import com.sparta.delivery.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantDto {
    private Long storeId;//식당아이디
    private String name; //식당이름

    //생성자
    public RestaurantDto (Long storeId) {
        this.storeId = storeId;
    }

    public RestaurantDto(Restaurant restaurant) {
        this.storeId = restaurant.getId();
        this.name = restaurant.getName();
    }
}