package com.sparta.delivery.cart.dto;

import com.sparta.delivery.menu.entity.Menu;
import lombok.Getter;

@Getter
public class CartItemDto {

    private final Long menuId;
    private final String menuName;
    private final int price;
    private final Long count;

    public CartItemDto(Menu menu, Long count) {
        this.menuId = menu.getId();
        this.menuName = menu.getName();
        this.price = menu.getPrice();
        this.count = count;
    }
}
