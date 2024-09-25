package com.sparta.delivery.menu.dto;

import lombok.Getter;

@Getter
public class MenuDeleteRequestDto {

    private Long restaurantId;

    public MenuDeleteRequestDto( Long restaurantId) {

        this.restaurantId = restaurantId;
    }
}
