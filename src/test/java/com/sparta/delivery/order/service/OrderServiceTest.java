package com.sparta.delivery.order.service;

import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.entity.CartItem;
import com.sparta.delivery.cart.repository.CartRepository;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.orders.dto.CombineDto;
import com.sparta.delivery.orders.dto.OrderDetailDto;
import com.sparta.delivery.orders.dto.OrderRequestDto;
import com.sparta.delivery.orders.dto.OrderResponseDto;
import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.orders.enums.OrderStatus;
import com.sparta.delivery.orders.repository.OrderDetailRepository;
import com.sparta.delivery.orders.repository.OrdersRepository;
import com.sparta.delivery.orders.service.OrderService;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.delivery.menu.enums.MenuStatus.AVAILABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Restaurant restaurant;
    private Cart cart;
    private Orders orders;
    private OrderRequestDto orderRequestDto;
    private List<CartItem> cartItems;

    @BeforeEach
    void setUp() {
        // 유저, 레스토랑, 카트 설정
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("테스트 유저");

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setOpenTime(LocalDateTime.now().minusHours(1).toLocalTime()); // 1시간 전
        restaurant.setCloseTime(LocalDateTime.now().plusHours(2).toLocalTime()); // 2시간 후
        restaurant.setMinOrderAmount(1000L);

        Menu menu1 = new Menu("메뉴 1", 1000,restaurant,AVAILABLE);
        Menu menu2 = new Menu("메뉴 2", 2000, restaurant,AVAILABLE);

        cart = new Cart(user);
        cartItems = new ArrayList<>();
        cartItems.add(new CartItem(cart, menu1, 1L));
        cartItems.add(new CartItem(cart, menu2, 1L));
        cart.getCartItems().addAll(cartItems);
        user.setCart(cart);

        orderRequestDto = new OrderRequestDto(restaurant.getId(), "테스트 주소", 3000L);
        // 주문 객체 생성
        orders = new Orders(user, "테스트 주소", "테스트 유저", restaurant, LocalDateTime.now(), OrderStatus.PENDING, (long) cartItems.size(), 3000L);
    }


    @Test
    void test1(){
        // Mock 설정
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));
        when(ordersRepository.save(any(Orders.class))).thenReturn(orders);


        // 메서드 실행
        ResponseEntity<CombineDto> response = orderService.requestOrder(1L, orderRequestDto, "테스트 유저");

        // 결과 검증
        assertNotNull(response);
        CombineDto combineDto = response.getBody();
        assertNotNull(combineDto);
        OrderResponseDto orderResponseDto = combineDto.getOrderResponseDto();
        List<OrderDetailDto> orderDetailDtos = combineDto.getOrderDetailDto();

        // 주문 정보 검증
        assertEquals(user.getId(), orderResponseDto.getUserId());
        assertEquals("테스트 주소", orderResponseDto.getAddress());
        assertEquals(3000L, orderResponseDto.getTotalPrice());

        // 상세 주문 정보 검증
        assertEquals(2, orderDetailDtos.size());
        assertEquals("메뉴 1", orderDetailDtos.get(0).getMenuName());
        assertEquals("메뉴 2", orderDetailDtos.get(1).getMenuName());

    }


}
