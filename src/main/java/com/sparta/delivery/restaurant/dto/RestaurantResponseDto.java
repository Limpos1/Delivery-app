package com.sparta.delivery.restaurant.dto;

import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.enums.RestaurantCategory;
import com.sparta.delivery.restaurant.enums.RestaurantStatus;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalTime;

@Getter
public class RestaurantResponseDto implements Serializable {

    private final Long id;
    private final String name;
    private final RestaurantCategory category;
    private final Long minOrderAmount;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final RestaurantStatus status;

    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.category = restaurant.getCategory();
        this.minOrderAmount = restaurant.getMinOrderAmount();
        this.openTime = restaurant.getOpenTime();
        this.closeTime = restaurant.getCloseTime();
        this.status = restaurant.getStatus();
    }
}