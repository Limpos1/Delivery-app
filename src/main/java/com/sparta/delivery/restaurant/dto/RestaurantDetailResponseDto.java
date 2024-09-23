package com.sparta.delivery.restaurant.dto;

import com.sparta.delivery.menu.dto.MenuSaveResponseDto;
import com.sparta.delivery.menu.dto.RestaurantDto;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.restaurant.entity.Restaurant;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RestaurantDetailResponseDto {
    private Long id;
    private String name;
    private Long minOrderAmount;
    private LocalTime openTime;
    private LocalTime closeTime;
    private List<MenuSaveResponseDto> menus;

    public RestaurantDetailResponseDto(Restaurant restaurant, List<Menu> availableMenus) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.minOrderAmount = restaurant.getMinOrderAmount();
        this.openTime = restaurant.getOpenTime();
        this.closeTime = restaurant.getCloseTime();
        this.menus = availableMenus.stream()
                .map(menu -> new MenuSaveResponseDto(menu.getName(), menu.getPrice(), new RestaurantDto(restaurant), menu.getId()))
                .collect(Collectors.toList());
    }
}