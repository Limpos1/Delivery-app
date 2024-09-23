package com.sparta.delivery.cart.util;

import com.sparta.delivery.cart.dto.CartItemDto;
import com.sparta.delivery.cart.entity.CartItem;

import java.util.List;

public class CartStreamUtil {

    public static List<CartItemDto> cartItemDtos(List<CartItem> cartItems){
        return cartItems.stream()
                .map(cartItem -> new CartItemDto(cartItem.getMenu(), cartItem.getCount()))
                .toList();
    }

    public static Long totalCount(List<CartItem> cartItems){
        return cartItems.stream()
                .mapToLong(CartItem::getCount)
                .sum();
    }
}
