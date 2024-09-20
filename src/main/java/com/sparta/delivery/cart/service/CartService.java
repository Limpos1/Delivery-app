
package com.sparta.delivery.cart.service;

import com.sparta.delivery.cart.dto.CartItemDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveRequestDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveResponseDto;
import com.sparta.delivery.cart.dto.cartupdate.CartUpdateResponseDto;
import com.sparta.delivery.cart.dto.cartviewall.CartViewAllResponseDto;
import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.repository.CartRepository;
import com.sparta.delivery.cart.util.CartOtherRestaurantUtil;
import com.sparta.delivery.cart.util.CartScheduleUtil;
import com.sparta.delivery.cart.util.CartStreamUtil;
import com.sparta.delivery.cart.util.FindRestaurantUtil;
import com.sparta.delivery.menu.entity.Menus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartSaveResponseDto saveCart(CartSaveRequestDto cartSaveRequestDto) {
        log.info("장바구니 저장 유저 확인");
        //userId로 먼저 유저 확인
        User user = userRepository.findById(cartSaveRequestDto.getUserId())
                .orElseThrow(() -> {
                        log.error("유저를 찾을 수 없습니다.");
                        return new IllegalArgumentException("User not found");
                });
        //cart가 생성되어 있는지 확인 없으면 orElse로 카트 생성
        Cart cart = cartRepository.findByUser(user)
                .orElse(new Cart(user));
        log.info("유저 ID : {},  장바구니ID : {}", user.getId(), cart.getId());

        CartScheduleUtil.timeClearCart(cartRepository);

        List<Menus> menus = FindRestaurantUtil.findRestuarant(cartSaveRequestDto.getMenuId(), menuRepository);

        Menus firstMenu = menus.get(0);
        Long restaurantId = firstMenu.getRestaurant().getId();

        CartOtherRestaurantUtil.otherRestaurant(cart, restaurantId);

        /* menuRepository에서 menuId를 찾아서 맞는지 확인하고 맞으면 Cart에 추가하고 수량 증가)*/
        for (Long menuId : cartSaveRequestDto.getMenuId()) {
            Menus menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> {
                        log.error("메뉴를 찾을 수 없습니다.");
                        return new IllegalArgumentException("Menu not found");});
            cart.addOrUpdateMenu(menu, cartSaveRequestDto.getCount());
        }

        Cart savedCart = cartRepository.save(cart);
        log.info("장바구니 저장 완료 장바구니ID : {}", savedCart.getId());

        List<CartItemDto> items = CartStreamUtil.cartItemDtos(savedCart.getCartItems());
        //CartSaveResponseDto 객체로 변환해서 반환
        return new CartSaveResponseDto(savedCart.getId(), user.getId(), items);
    }

    //장바구니 조회
    public CartViewAllResponseDto getViewAllCart(Long userId) {
        log.info("장바구니 조회");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        log.info("장바구니 조회 성공");

        List<CartItemDto> items = CartStreamUtil.cartItemDtos(cart.getCartItems());

        Long totalCount = CartStreamUtil.totalCount(cart.getCartItems());

        log.info("장바구니 조회 완료");
        //CartViewAllResponseDto 객체 생성 반환
        return new CartViewAllResponseDto(cart.getId(), user.getId(), items, totalCount, cart.getLastupdated());
    }

    //cartUpdate
    @Transactional
    public CartUpdateResponseDto updateCart(Long userId, Long menuId, Long count) {
        log.info("장바구니 수정 요청");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Menus menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu Not Found"));
        //count가 0개면은 메뉴 삭제
        if (count <= 0) {
            cart.removeMenu(menu);
        } else {
            cart.addOrUpdateMenu(menu, count);
        }
        //update 데이터 저장
        cartRepository.save(cart);
        log.info("장바구니 수정 완료");

        List<CartItemDto> items = CartStreamUtil.cartItemDtos(cart.getCartItems());

        Long totalCount = CartStreamUtil.totalCount(cart.getCartItems());

        return new CartUpdateResponseDto(cart.getId(), user.getId(), items, totalCount, cart.getLastupdated());
    }

    @Transactional
    public void deleteCart(Long userId) {
        log.info("장바구니 삭제 요청");
        if(!cartRepository.existsById(userId)){
            throw new IllegalArgumentException("User not found");
        }
        cartRepository.deleteById(userId);
        log.info("장바구니 삭제 완료");

    }
}



