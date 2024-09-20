
package com.sparta.delivery.cart.service;

import com.sparta.delivery.cart.dto.CartItemDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveRequestDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveResponseDto;
import com.sparta.delivery.cart.dto.cartupdate.CartUpdateResponseDto;
import com.sparta.delivery.cart.dto.cartviewall.CartViewAllResponseDto;
import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.entity.CartItem;
import com.sparta.delivery.cart.repository.CartRepository;
import com.sparta.delivery.menu.entity.Menus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        // cart 유효 시간 1일 지나면 초기화
        if (cart.getLastupdated().plusDays(1).isBefore(LocalDateTime.now())) {
            cart.clearCart();
        }
        //첫번째 주문으로 가게 정보를 확인
        Menus firstMenu = menuRepository.findById(cartSaveRequestDto.getMenuId()
                        .get(0))
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        Long restaurantId = firstMenu.getRestaurant().getId();

        //장바구니에 다른 가게의 메뉴가 들어오면 초기화
        if(!cart.getCartItems().isEmpty()){
            Menus viewMenu = cart.getCartItems().get(0).getMenu();
            Long viewRestaurantId = viewMenu.getRestaurant().getId();

            if(!viewRestaurantId.equals(restaurantId)){
                cart.clearCart();
            }
        }
        //메뉴 추가
        for (Long menuId : cartSaveRequestDto.getMenuId()) {
            Menus menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
            cart.addOrUpdateMenu(menu, cartSaveRequestDto.getCount());
        }

        Cart savedCart = cartRepository.save(cart);

        return new CartSaveResponseDto(savedCart.getId(), user.getId(),
                savedCart.getCartItems().stream()
                        .map(CartItem::getMenu)
                        .collect(Collectors.toList()));
    }

    public CartViewAllResponseDto getViewAllCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        //장바구니 항목 Dto로 전환
        List<CartItemDto> items = cart.getCartItems().stream()
                .map(cartItem -> new CartItemDto(cartItem.getMenu(), cartItem.getCount()))
                .toList();
        // 장바구니 항복 총 수량 및 갱신 시간
        return new CartViewAllResponseDto(cart.getId(), user.getId(), items,
                cart.getCartItems().stream()
                        .mapToLong(CartItem::getCount)
                        .sum(),
                cart.getLastupdated());
    }

    public CartUpdateResponseDto updateCart(Long userId, Long menuId, Long count) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Menus menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu Not Found"));

        if (count <= 0) {
            cart.removeMenu(menu);
        } else {
            cart.addOrUpdateMenu(menu, count);
        }

        cartRepository.save(cart);

        List<CartItemDto> menuiIems = cart.getCartItems().stream()
                .map(cartItem -> new CartItemDto(cartItem.getMenu() ,cartItem.getCount()))
                .toList();

        Long totalCount = cart.getCartItems().stream()
                .mapToLong(CartItem::getCount)
                .sum();

        return new CartUpdateResponseDto(cart.getId(),
                user.getId(),
                menuiIems,
                totalCount,
                cart.getLastupdated());
    }

    public void deleteCart(Long userId) {
        if(!cartRepository.existsById(userId)){
            throw new IllegalArgumentException("User not found");
        } else {
            cartRepository.deleteById(userId);
        }
    }
}



