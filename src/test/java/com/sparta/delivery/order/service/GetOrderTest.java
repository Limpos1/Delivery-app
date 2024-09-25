package com.sparta.delivery.order.service;

import com.sparta.delivery.orders.dto.CombineDto;
import com.sparta.delivery.orders.dto.OrderDetailDto;
import com.sparta.delivery.orders.dto.OrderResponseDto;
import com.sparta.delivery.orders.entity.OrderDetail;
import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.orders.enums.OrderStatus;
import com.sparta.delivery.orders.repository.OrderDetailRepository;
import com.sparta.delivery.orders.repository.OrdersRepository;
import com.sparta.delivery.orders.service.OrderService;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.user.entity.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class GetOrderTest {
    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @InjectMocks
    private OrderService orderService;

    private Orders order;
    private List<OrderDetail> orderDetails;
    private User user;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        // 유저 및 레스토랑 설정
        user = new User();
        user.setId(1L);
        user.setName("테스트 유저");

        restaurant = new Restaurant();
        restaurant.setId(1L);

        // 주문 객체 설정
        order = new Orders(user, "테스트 주소", "테스트 유저", restaurant, LocalDateTime.now(), OrderStatus.PENDING, 2L, 3000L);
        order.setId(1L);

        // 주문 상세 객체 설정
        orderDetails = new ArrayList<>();
        OrderDetail orderDetail1 = new OrderDetail(order, 1L, "메뉴 1", restaurant.getId(),1000 , LocalDateTime.now());
        OrderDetail orderDetail2 = new OrderDetail(order, 2L, "메뉴 2", restaurant.getId(),2000, LocalDateTime.now());
        orderDetails.add(orderDetail1);
        orderDetails.add(orderDetail2);
    }

    @Test
    void getOrder_성공() {
        // Given: 주문이 존재할 때의 Mock 설정
        given(ordersRepository.findById(anyLong())).willReturn(Optional.of(order));
        given(orderDetailRepository.findAllByOrdersId(order)).willReturn(orderDetails);

        // When: getOrder 메서드 호출
        ResponseEntity<CombineDto> response = orderService.getOrder(1L);

        // Then: 결과 검증
        assertNotNull(response);
        CombineDto combineDto = response.getBody();
        assertNotNull(combineDto);

        // 주문 정보 검증
        OrderResponseDto orderResponseDto = combineDto.getOrderResponseDto();
        assertEquals(user.getId(), orderResponseDto.getUserId());
        assertEquals("테스트 주소", orderResponseDto.getAddress());
        assertEquals(3000L, orderResponseDto.getTotalPrice());

        // 주문 상세 정보 검증
        List<OrderDetailDto> orderDetailDtos = combineDto.getOrderDetailDto();
        assertEquals(2, orderDetailDtos.size());

        OrderDetailDto detailDto1 = orderDetailDtos.get(0);
        assertEquals("메뉴 1", detailDto1.getMenuName());
        assertEquals(1000L, detailDto1.getPrice());

        OrderDetailDto detailDto2 = orderDetailDtos.get(1);
        assertEquals("메뉴 2", detailDto2.getMenuName());
        assertEquals(2000L, detailDto2.getPrice());
    }

    @Test
    void getOrder_실패_주문없음() {
        // Given: 주문이 없을 때의 Mock 설정
        given(ordersRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then: 예외 발생 검증
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrder(1L);
        });

        assertEquals("Order not found", exception.getMessage());
    }

}
