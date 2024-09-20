package com.sparta.delivery.cart.util;

import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class CartScheduleUtil {

    public static void timeClearCart(CartRepository cartRepository) {
      log.info("장바구니 초기화 작업");
        List<Cart> carts = cartRepository.findAll();
        for (Cart cart : carts) {
            if(cart.getLastupdated().plusDays(1).isBefore(LocalDateTime.now())){
                log.info("장바구니 초기화 ID : {}", cart.getId());
                cart.clearCart();
                cartRepository.save(cart);
            }
        }
    }
}
