package com.sparta.delivery.cart.dto.cartupdate;

import com.sparta.delivery.cart.dto.CartItemDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CartUpdateResponseDto {

    private final Long id;
    private final Long userId;
    private final List<CartItemDto> menus;
    private final Long count;
    private final LocalDateTime lastupdated;

    public CartUpdateResponseDto(Long id, Long userId, List<CartItemDto> menus, Long count, LocalDateTime lastupdated) {
        this.id = id;
        this.userId = userId;
        this.menus = menus;
        this.count = count;
        this.lastupdated = lastupdated;
    }
}
