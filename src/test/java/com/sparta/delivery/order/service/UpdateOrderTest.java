package com.sparta.delivery.order.service;

import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.orders.enums.OrderStatus;
import com.sparta.delivery.orders.repository.OrdersRepository;
import com.sparta.delivery.orders.service.OrderService;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpdateOrderTest {
    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Orders order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setRole(UserRole.OWNER); // 사장님으로 설정

        order = new Orders();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING); // 초기 상태 설정
    }

    @Test
    void updateOrder_success() {
        // Given
        Long userId = user.getId();
        Long orderId = order.getId();
        String newStatus = "COMPLETED"; // 변경할 상태

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(ordersRepository.findById(orderId)).willReturn(Optional.of(order));

        // When
        OrderStatus updatedStatus = orderService.updateOrder(userId, orderId, newStatus);

        // Then
        assertEquals(OrderStatus.COMPLETED, updatedStatus);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertEquals(LocalDate.now(),order.getModifyTime().toLocalDate());
        verify(ordersRepository).save(order);
    }

    @Test
    void updateOrder_orderNotFound() {
        // Given
        Long userId = user.getId();
        Long orderId = order.getId();
        String newStatus = "COMPLETED";

        given(ordersRepository.findById(orderId)).willReturn(Optional.empty()); // 주문이 없음
        given(userRepository.findById(userId)).willReturn(Optional.of(user));


        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrder(userId, orderId, newStatus);
        });
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void updateOrder_userNotFound() {
        // Given
        Long userId = 2L; // 존재하지 않는 유저 ID
        Long orderId = order.getId();
        String newStatus = "COMPLETED";

        given(userRepository.findById(userId)).willReturn(Optional.empty()); // 유저가 없음

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrder(userId, orderId, newStatus);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateOrder_userNotOwner() {
        // Given
        user.setRole(UserRole.USER); // 사장님이 아님
        Long userId = user.getId();
        Long orderId = order.getId();
        String newStatus = "COMPLETED";

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(ordersRepository.findById(orderId)).willReturn(Optional.of(order));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrder(userId, orderId, newStatus);
        });
        assertEquals("가게 사장님만 변경할 수 있습니다..", exception.getMessage());
    }

    @Test
    void updateOrder_invalidStatus() {
        // Given
        Long userId = user.getId();
        Long orderId = order.getId();
        String invalidStatus = "INVALID_STATUS"; // 잘못된 상태

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(ordersRepository.findById(orderId)).willReturn(Optional.of(order));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrder(userId, orderId, invalidStatus);
        });
        assertEquals("잘못된 요청입니다.", exception.getMessage());
    }
}
