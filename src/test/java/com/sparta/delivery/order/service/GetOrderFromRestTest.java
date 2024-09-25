package com.sparta.delivery.order.service;

import com.sparta.delivery.orders.dto.OrderResponseDto;
import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.orders.enums.OrderStatus;
import com.sparta.delivery.orders.repository.OrdersRepository;
import com.sparta.delivery.orders.service.OrderService;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GetOrderFromRestTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OrdersRepository ordersRepository;

    @InjectMocks
    private OrderService orderService;

    private User owner;
    private User user;
    private Restaurant restaurant;

    @Test
    void getOrderFromRest_restaurantNotFound() {
        // Given
        Long userId = 1L;
        Long restaurantId = 1L;
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrderFromRest(userId, restaurantId);
        });
        assertEquals("등록된 식당이 없습니다.", exception.getMessage());
    }

    @Test
    void getOrderFromRest_notOwner() {
        // Given
        Long userId = 1L;
        Long restaurantId = 1L;
        owner = new User();
        owner.setId(2L); // 다른 사장님
        restaurant = new Restaurant();
        restaurant.setOwnerId(owner);
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrderFromRest(userId, restaurantId);
        });
        assertEquals("해당 가게의 사장님만 조회가능합니다.", exception.getMessage());
    }

    @Test
    void getOrderFromRest_success() {
        // Given
        Long userId = 1L;
        Long restaurantId = 1L;
        owner = new User();
        owner.setId(userId); // 올바른 사장님
        restaurant = new Restaurant();
        restaurant.setOwnerId(owner);
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));

        // Sample orders
        user = new User();
        user.setId(2L);
        Orders order1 = new Orders();
        order1.setUserId(user); // 주문자 ID
        order1.setAddress("주소 1");
        order1.setName("주문자 1");
        order1.setOrderTime(LocalDateTime.now());
        order1.setStatus(OrderStatus.PENDING);
        order1.setId(1L);
        order1.setCount(1L);
        order1.setTotalPrice(10000L);
        order1.setRestaurant(restaurant);

        List<Orders> orders = new ArrayList<>();
        orders.add(order1);

        given(ordersRepository.findAllByRestaurantId(restaurantId)).willReturn(orders);

        // When
        ResponseEntity<List<OrderResponseDto>> response = orderService.getOrderFromRest(userId, restaurantId);

        // Then
        assertEquals(1, response.getBody().size());
        assertEquals("주소 1", response.getBody().get(0).getAddress());
        assertEquals("주문자 1", response.getBody().get(0).getName());
        assertEquals(OrderStatus.PENDING, response.getBody().get(0).getStatus());

        // Verify interactions
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(ordersRepository, times(1)).findAllByRestaurantId(restaurantId);
    }
}
