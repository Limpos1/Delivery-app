package com.sparta.delivery.cart.dto.cartsave;

import com.sparta.delivery.menu.entity.Menu;
import lombok.Getter;

import java.util.List;

@Getter
public class CartSaveResponseDto {

    private final Long cartId;
    private final Long userId;
    private final List<Menu> menus;



    public CartSaveResponseDto(Long cartId, Long userId, List<Menu> menus) {
        this.cartId = cartId;
        this.userId = userId;
        this.menus = menus;
    }
}
