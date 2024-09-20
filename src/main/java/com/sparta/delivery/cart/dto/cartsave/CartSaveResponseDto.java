package com.sparta.delivery.cart.dto.cartsave;

import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.entity.Menus;
import lombok.Getter;

import java.util.List;

@Getter
public class CartSaveResponseDto {

    private final Long cartId;
    private final Long userId;
    private final List<Menus> menus;



    public CartSaveResponseDto(Long cartId, Long userId, List<Menus> menus) {
        this.cartId = cartId;
        this.userId = userId;
        this.menus = menus;
    }
}
