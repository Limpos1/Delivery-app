package com.sparta.delivery.cart.service;

import com.sparta.delivery.cart.dto.cartsave.CartSaveRequestDto;
import com.sparta.delivery.cart.dto.cartsave.CartSaveResponseDto;
import com.sparta.delivery.cart.dto.cartupdate.CartUpdateResponseDto;
import com.sparta.delivery.cart.dto.cartviewall.CartViewAllResponseDto;
import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.repository.CartRepository;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.enums.MenuStatus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.enums.UserStatus;
import com.sparta.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Restaurant restaurant;
    private Menu menu1, menu2;
    private Cart cart;
    private CartSaveRequestDto cartSaveRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L); // user 객체에 id 설정
        user.setEmail("test@example.com");            // 이메일 설정
        user.setPassword("securepassword");           // 비밀번호 설정
        user.setRole(UserRole.USER);                  // 역할 설정 (USER)
        user.setStatus(UserStatus.NON_WITHDRAWAL);           // 예시로 이메일 설정

        // Restaurant 생성 및 restaurantId 설정
        restaurant = new Restaurant();
        ReflectionTestUtils.setField(restaurant, "id", 1L);  // 임시로 restaurantId를 1L로 설정

        menu1 = new Menu("메뉴 1", 10000, restaurant , MenuStatus.AVAILABLE);
        menu2 = new Menu("메뉴 2", 20000, restaurant , MenuStatus.AVAILABLE);

        cart = new Cart();
        cart.addOrUpdateMenu(menu1, 2L);
        cart.addOrUpdateMenu(menu2, 1L);

        List<Long> menuId = Arrays.asList(1L, 2L);
        cartSaveRequestDto = new CartSaveRequestDto(1L, menuId, null);
    }

    @Test
    void saveCart() {
        // 유저, 메뉴, 카트에 대한 Mock 설정
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
        when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu1,menu2));
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu1));  // menu1 반환
        when(menuRepository.findById(2L)).thenReturn(Optional.of(menu2));  // menu2 반환
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);  // 카트 저장 Mock 설정

        // saveCart 메서드 호출
        CartSaveResponseDto response = cartService.saveCart(cartSaveRequestDto);

        // 검증
        assertNotNull(response);  // 응답이 null이 아님을 확인
        assertEquals(user.getId(), response.getUserId());  // 유저 ID가 일치하는지 확인
        assertEquals(2, response.getMenus().size());  // 메뉴의 개수가 2개인지 확인

        // cartRepository의 save 메서드가 호출되었는지 확인
        verify(cartRepository).save(any(Cart.class));
    }


    @Test
    void getViewAllTest(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));

        CartViewAllResponseDto response = cartService.getViewAllCart(user.getId());

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(2, response.getMenus().size());
        assertEquals(3L, response.getCount());
    }

    @Test
    void updateCart(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu1));

        CartUpdateResponseDto response = cartService.updateCart(user.getId(),1L,3L);

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(2, response.getMenus().size());
        assertEquals(4L, response.getCount());

        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void deleteCart(){
        when(cartRepository.existsById(anyLong())).thenReturn(true);

        cartService.deleteCart(user.getId());

        verify(cartRepository).deleteById(user.getId());
    }
}