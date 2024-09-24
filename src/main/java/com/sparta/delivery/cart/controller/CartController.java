package com.sparta.delivery.cart.controller;

import com.sparta.delivery.cart.dto.cartsave.CartSaveRequestDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveResponseDto;
import com.sparta.delivery.cart.dto.cartupdate.CartUpdateResponseDto;
import com.sparta.delivery.cart.dto.cartviewall.CartViewAllResponseDto;
import com.sparta.delivery.cart.service.CartService;
import com.sparta.delivery.etc.annotation.Sign;
import com.sparta.delivery.etc.common.SignUser;
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

    @GetMapping("/carts")
    public ResponseEntity<CartViewAllResponseDto> getViewAllCarts(@Sign SignUser signUser){
        return ResponseEntity.ok(cartService.getViewAllCart(signUser.getId()));
    }

    @PatchMapping("/carts/update/{menuId}")
    public ResponseEntity<CartUpdateResponseDto> updateCart(
            @Sign SignUser signUser,
            @PathVariable Long menuId,
            @RequestParam Long count
    ){
        return ResponseEntity.ok(cartService.updateCart(signUser.getId(), menuId, count));
    }

    @DeleteMapping("/carts")
    public void deleteCart(@Sign SignUser signUser){
        cartService.deleteCart(signUser.getId());
    }
}
