package com.sparta.delivery.cart.dto.cartsave;


import lombok.Getter;

import java.util.List;

@Getter
public class CartSaveRequestDto {
    private List<Long> menuId;
    private Long count;


    public CartSaveRequestDto(List<Long> menuId, Long count) {
        this.menuId = menuId;
        this.count = count;
    }
}
