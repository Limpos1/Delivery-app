package com.sparta.delivery.restaurant.dto;

import com.sparta.delivery.menu.dto.MenuSaveResponseDto;
import com.sparta.delivery.menu.dto.RestaurantDto;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.restaurant.entity.Restaurant;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RestaurantDetailResponseDto {
    private Long id;
    private String name;
    private Long minOrderAmount;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private List<MenuSaveResponseDto> menus;

    public RestaurantDetailResponseDto(Restaurant restaurant, List<Menu> availableMenus) {
        if (restaurant == null) {
            throw new IllegalArgumentException("가게 정보가 없습니다.");
        }
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.minOrderAmount = restaurant.getMinOrderAmount();
        this.openTime = restaurant.getOpenTime();
        this.closeTime = restaurant.getCloseTime();

        RestaurantDto restaurantDto = new RestaurantDto(restaurant);
        this.menus = availableMenus.stream()
                .map(menu -> new MenuSaveResponseDto(menu.getName(), menu.getPrice(), restaurantDto, menu.getId()))
                .collect(Collectors.toList());
    }
}