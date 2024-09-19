package com.sparta.delivery.cart.controller;

import com.sparta.delivery.cart.dto.cartsave.CartSaveRequestDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveResponseDto;
import com.sparta.delivery.cart.dto.cartviewall.CartViewAllResponseDto;
import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/carts/add")
    public ResponseEntity<CartSaveResponseDto> saveCart(@RequestBody CartSaveRequestDto cartSaveRequestDto){
        return ResponseEntity.ok(cartService.saveCart(cartSaveRequestDto));
    }

}
