package com.sparta.delivery.order.dto;

import com.sparta.delivery.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDto {
    private Long restaurantId; // 식당 아이디
    private Long menuId; // 메뉴 아이디
    private Long price;
    private String address; // 주소


    public OrderRequestDto(Long restaurantId, Long menuId, String address, Long price) {
        this.restaurantId = restaurantId;
        this.menuId = menuId;
        this.address = address;
        this.price = price;
    }
}
