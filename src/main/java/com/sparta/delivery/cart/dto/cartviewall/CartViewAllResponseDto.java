package com.sparta.delivery.cart.dto.cartviewall;

import lombok.Getter;

import java.util.List;

@Getter
public class CartViewAllResponseDto {

    private final Long id;
    private final User userId;
    private final List<Menu> menus;
    private final Long Count;
}
