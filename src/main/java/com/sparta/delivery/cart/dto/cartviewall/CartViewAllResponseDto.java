package com.sparta.delivery.cart.dto.cartviewall;

import com.sparta.delivery.cart.dto.CartItemDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CartViewAllResponseDto {

    private final Long id;
    private final Long userId;
    private final List<CartItemDto> menus;
    private final Long Count;
    private final LocalDateTime LastUpdated;

    public CartViewAllResponseDto(Long id, Long userId, List<CartItemDto> menus, Long count, LocalDateTime lastUpdated) {
        this.id = id;
        this.userId = userId;
        this.menus = menus;
        this.Count = count;
        this.LastUpdated = lastUpdated;
    }
}
