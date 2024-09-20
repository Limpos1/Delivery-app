
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        /* cart 유효 시간 1일 지나면 초기화
        isBefore은 plusDay보다 Lastupdate가 과거 즉 1일 전이면 초기화 */
        if (cart.getLastupdated().plusDays(1).isBefore(LocalDateTime.now())) {
            cart.clearCart();//redis //scheduling
        }
        /*첫번째 주문으로 가게 정보를 확인
        findById로 메뉴 Id를 받아오고 get(0)으로 메뉴 리스트에서 첫번째 메뉴를 가져옴
        firstMenu로 getRestaurant에 접근해서 getId로 레스토랑 id를 가져옴 */
        Menus firstMenu = menuRepository.findById(cartSaveRequestDto.getMenuId().get(0))
                .orElseThrow(() ->{
                    log.error("메뉴를 찾을 수 없습니다.");
                    return new IllegalArgumentException("Menu not found");});
        Long restaurantId = firstMenu.getRestaurant().getId();
        log.info("레스토랑ID : {}" ,restaurantId);
        /* !를 통해서 카트(장바구니)에 cartItem이 있는지 확인
        카트에서 첫번째 메뉴와 레스토랑 Id를 확인 위에 restaurantId로 다른 가게 메뉴가 있는지 확인하고
        다른 가게 메뉴가 들어오면 장바구니 초기화*/
        if(!cart.getCartItems().isEmpty()){
            Menus viewMenu = cart.getCartItems().get(0).getMenu();
            Long viewRestaurantId = viewMenu.getRestaurant().getId();

            if(!viewRestaurantId.equals(restaurantId)){
                log.info("다른 가계의 메뉴가 추가되었습니다.");
                cart.clearCart();
            }
        }
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
        //장바구니에 있는 CartItem을 가저옴
        List<CartItem> cartItems = savedCart.getCartItems();
        /*각 CartItem에서 메뉴를 리스트로 변환
        for문 대신 stream을 사용 / .map으로 CartItem 객체에서 Menu 객체 추출/
        stream으로 변환된 된 타입을 .collect로 다시 List타입으로 변환
         */
        List<Menus> menus = cartItems.stream()
                .map(CartItem::getMenu)
                .toList();
        //CartSaveResponseDto 객체로 변환해서 반환
        return new CartSaveResponseDto(savedCart.getId(), user.getId(), menus);
    }

    //장바구니 조회
    public CartViewAllResponseDto getViewAllCart(Long userId) {
        log.info("장바구니 조회");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        log.info("장바구니 조회 성공");
        /*장바구니 항목 Dto로 전환
        cart에서ㅏ getCartItem 리스트를 가저오고 .map을 통해서
        cartItem에서 menu와 count를 추출해서 CartItemDto로 변환
         */
        List<CartItemDto> items = cart.getCartItems().stream()
                .map(cartItem -> new CartItemDto(cartItem.getMenu(), cartItem.getCount()))
                .toList();
        /*cart에서ㅏ getCartItem 리스트를 가저오고 .map을 통해서
        mapToLong으로 CartItem에서 count를 추출해서 Long 타입으로 변환*/
        Long totalCount = cart.getCartItems().stream()
                .mapToLong(CartItem::getCount)
                .sum();

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
        /*cart에서ㅏ getCartItem 리스트를 가저오고 .map을 통해서
        cartItem에서 menu와 count를 추출해서 CartItemDto로 변환*/
        List<CartItemDto> menuItems = cart.getCartItems().stream()
                .map(cartItem -> new CartItemDto(cartItem.getMenu() ,cartItem.getCount()))
                .toList();

        /*cart에서ㅏ getCartItem 리스트를 가저오고 .map을 통해서
        mapToLong으로 CartItem에서 count를 추출해서 Long 타입으로 변환*/
        Long totalCount = cart.getCartItems().stream()
                .mapToLong(CartItem::getCount)
                .sum();

        return new CartUpdateResponseDto(cart.getId(), user.getId(), menuItems, totalCount, cart.getLastupdated());
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



