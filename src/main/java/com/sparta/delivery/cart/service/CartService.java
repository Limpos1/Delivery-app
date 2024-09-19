
package com.sparta.delivery.cart.service;

import com.sparta.delivery.cart.dto.cartsave.CartSaveRequestDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveResponseDto;
import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.repository.CartRepository;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;


    public CartSaveResponseDto saveCart(CartSaveRequestDto cartSaveRequestDto) {
        //userId로 먼저 유저 확인
        User user = userRepository.findById(cartSaveRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        //cart가 생성되어 있는지 확인 없으면 orElse로 카트 생성
        Cart cart = cartRepository.findByUser(user)
                .orElse(new Cart(user, cartSaveRequestDto.getCount()));
        // cart 유효 시간 1일 지나면 초기화
        if(cart.getLastUpdated().plusDays(1).isBefore(LocalDateTime.now())){
            cart.clearCart();
        }
        //첫번째 주문으로 가게 정보를 확인
        Menu firstMenu = menuRepository.findById(cartSaveRequestDto.getMenuId()
                .get(0))
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        Long restaurantId = firstMenu.getRestaurant().getId();

        //장바구니에 다른 가게의 메뉴가 들어오면 초기화
        if(!cart.getMenus().isEmpty() && !cart.getMenus().get(0).getId().equals(restaurantId)){
            cart.clearCart();
        }

        //메뉴 추가
        for(Long menuId : cartSaveRequestDto.getMenuId()){
            Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
            cart.addMenu(menu);
        }

        Cart savedCart = cartRepository.save(cart);
        return new CartSaveResponseDto(savedCart.getId(),user.getId(),savedCart.getMenus());
        //카트 저장

    }
}



