package com.sparta.delivery.restaurant.dto;

import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.enums.RestaurantStatus;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class RestaurantResponseDto implements Serializable {

    private final Long id;
    private final String name;
    private final Long minOrderAmount;
    private final LocalDateTime openTime;
    private final LocalDateTime closeTime;
    private final RestaurantStatus status;

    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.minOrderAmount = restaurant.getMinOrderAmount();
        this.openTime = restaurant.getOpenTime();
        this.closeTime = restaurant.getCloseTime();
        this.status = restaurant.getStatus();
    }
}