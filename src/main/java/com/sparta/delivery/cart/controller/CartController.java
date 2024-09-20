package com.sparta.delivery.cart.controller;

import com.sparta.delivery.cart.dto.cartsave.CartSaveRequestDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveResponseDto;
import com.sparta.delivery.cart.dto.cartupdate.CartUpdateResponseDto;
import com.sparta.delivery.cart.dto.cartviewall.CartViewAllResponseDto;
import com.sparta.delivery.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private Long count;

    @PostMapping("/carts/add")
    public ResponseEntity<CartSaveResponseDto> saveCart(@RequestBody CartSaveRequestDto cartSaveRequestDto){
        return ResponseEntity.ok(cartService.saveCart(cartSaveRequestDto));
    }

    @GetMapping("/carts/{userId}")
    public ResponseEntity<CartViewAllResponseDto> getViewAllCarts(@PathVariable Long userId){
        return ResponseEntity.ok(cartService.getViewAllCart(userId));
    }

    @PatchMapping("/carts/{userId}/update/{menuId}")
    public ResponseEntity<CartUpdateResponseDto> updateCart(
            @PathVariable Long userId,
            @PathVariable Long menuId,
            @RequestParam Long count
    ){
        return ResponseEntity.ok(cartService.updateCart(userId, menuId, count));
    }
}
