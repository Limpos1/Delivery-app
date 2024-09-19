package com.sparta.delivery.cart.dto.cartsave;

import com.sparta.delivery.menu.entity.Menu;
import lombok.Getter;

import java.util.List;

@Getter
public class CartSaveRequestDto {

    private Long userId;
    private List<Long> menuId;
    private Long count;
}
