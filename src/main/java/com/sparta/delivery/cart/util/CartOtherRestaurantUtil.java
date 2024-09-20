package com.sparta.delivery.cart.util;

import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.menu.entity.Menus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CartOtherRestaurantUtil {

    public static void otherRestaurant(Cart cart, Long restaurantId) {
        if(!cart.getCartItems().isEmpty()){
            Menus viewMenu = cart.getCartItems().get(0).getMenu();
            Long viewRestaurantId = viewMenu.getRestaurant().getId();

           if(!restaurantId.equals(viewRestaurantId)){
               log.info("다른 가게의 메뉴가 추가되었습니다");
               cart.clearCart();
           }
        }
    }
}
