
package com.sparta.delivery.cart.service;

import com.sparta.delivery.cart.dto.CartItemDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveRequestDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveResponseDto;
import com.sparta.delivery.cart.dto.cartupdate.CartUpdateResponseDto;
import com.sparta.delivery.cart.dto.cartviewall.CartViewAllResponseDto;
import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.repository.CartRepository;
import com.sparta.delivery.cart.util.CartStreamUtil;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Cart> redisTemplate;

    @Transactional
    public CartSaveResponseDto saveCart(Long userId, CartSaveRequestDto cartSaveRequestDto) {
        //userId로 먼저 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String cartKey = "cart:" + user.getId();

        Cart cart = redisTemplate.opsForValue().get(cartKey);
        if (cart == null) {
            cart = cartRepository.findByUser(user).orElse(new Cart());
        }

        List<Menu> menus = menuRepository.findAllByIdIn(cartSaveRequestDto.getMenuId());

        Menu firstMenu = menus.get(0);
        Long restaurantId = firstMenu.getRestaurant().getId();

        if(!cart.getCartItems().isEmpty()){
            Menu existingMenu = cart.getCartItems().get(0).getMenu();
            Long existingRestaurantId = existingMenu.getRestaurant().getId();

            if(!restaurantId.equals(existingRestaurantId)){
                cart.clearCart();
            }
        }

        /* menuRepository에서 menuId를 찾아서 맞는지 확인하고 맞으면 Cart에 추가하고 수량 증가)*/
        for (Long menuId : cartSaveRequestDto.getMenuId()) {
            Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
            cart.addOrUpdateMenu(menu, cartSaveRequestDto.getCount());
        }

        redisTemplate.opsForValue().set(cartKey, cart, Duration.ofDays(1));

        Cart savedCart = cartRepository.save(cart);

        List<CartItemDto> items = CartStreamUtil.cartItemDtos(savedCart.getCartItems());
        //CartSaveResponseDto 객체로 변환해서 반환
        return new CartSaveResponseDto(savedCart.getId(), user.getId(), items);
    }

    //장바구니 조회
    public CartViewAllResponseDto getViewAllCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        List<CartItemDto> items = CartStreamUtil.cartItemDtos(cart.getCartItems());

        Long totalCount = CartStreamUtil.totalCount(cart.getCartItems());


        //CartViewAllResponseDto 객체 생성 반환
        return new CartViewAllResponseDto(cart.getId(), user.getId(), items, totalCount, cart.getLastupdated());
    }

    //cartUpdate
    @Transactional
    public CartUpdateResponseDto updateCart(Long userId, Long menuId, Long count) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu Not Found"));
        //count가 0개면은 메뉴 삭제
        if (count <= 0) {
            cart.removeMenu(menu);
        } else {
            cart.addOrUpdateMenu(menu, count);
        }
        //update 데이터 저장
        cartRepository.save(cart);


        List<CartItemDto> items = CartStreamUtil.cartItemDtos(cart.getCartItems());

        Long totalCount = CartStreamUtil.totalCount(cart.getCartItems());

        return new CartUpdateResponseDto(cart.getId(), user.getId(), items, totalCount, cart.getLastupdated());
    }

    @Transactional
    public void deleteCart(Long userId) {

        if(!cartRepository.existsById(userId)){
            throw new IllegalArgumentException("User not found");
        }
        cartRepository.deleteById(userId);


    }
}



