package com.sparta.delivery;

import com.sparta.delivery.order.dto.OrderDetailDto;
import com.sparta.delivery.order.dto.OrderRequestDto;
import com.sparta.delivery.order.dto.OrderResponseDto;
import com.sparta.delivery.order.entity.CombineDto;
import com.sparta.delivery.order.entity.OrderDetail;
import com.sparta.delivery.order.entity.Orders;
import com.sparta.delivery.order.enums.OrderStatus;
import com.sparta.delivery.order.repository.OrderDetailRepository;
import com.sparta.delivery.order.repository.OrderRepository;
import com.sparta.delivery.order.service.OrderService;
import com.sparta.delivery.restorant.entity.Restaurant;
import com.sparta.delivery.restorant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void test1(){
        Long userId = 1L; //주문자 아이디
        Long restaurantId = 2L; // 식당 아이디
        Long menuId = 3L; // 메뉴 아이디
        String address="여기저기"; // 주소

        // 테스트용 유저 데이터 생성 및 저장
        User user = new User();
        user.setId(userId);
        user.setName("박지민");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");  // 비밀번호는 암호화된 상태로 저장해야 함
        user.setRole(UserRole.USER);  // USER 또는 OWNER 권한 설정
        userRepository.save(user);

        //테스트용 식당 생성
        Restaurant rest = new Restaurant();
        rest.setId(restaurantId);
        rest.setName("햄버거 가게");
        rest.setMinOrderAmount(15000L);

        //오후에 열고 새벽에 닫으면 오류가 발생.
        //날짜가 필요함, 따라서 LocalTime을 LocalDateTime으로 변경함.
        rest.setOpenTime(LocalDateTime.of(2024, 9,21,12,30));
        rest.setCloseTime(LocalDateTime.of(2024,9,22,5,30));
        restaurantRepository.save(rest);
        

        OrderRequestDto requestDto = new OrderRequestDto(userId, restaurantId, menuId, address, 15000L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(rest));

        Orders mockOrder = new Orders(user, address, user.getName(), LocalDateTime.now(), OrderStatus.PENDING);
        mockOrder.setId(1L);
        when(orderRepository.save(any(Orders.class))).thenReturn(mockOrder);

        OrderDetail mockOrderDetail = new OrderDetail(mockOrder, menuId, restaurantId, 1L, requestDto.getPrice());
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(mockOrderDetail);

        Long orderId = mockOrderDetail.getOrdersId().getId();

        ResponseEntity<CombineDto> response = orderService.orderrequest(requestDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        CombineDto resDto = response.getBody();
        assert resDto != null;



        // 검증: 주문 정보
        OrderResponseDto orderResponse = resDto.getOrderResponseDto();
        System.out.println(orderResponse.getUserId());
        assertEquals(userId, orderResponse.getUserId());

        assertEquals(address, orderResponse.getAddress());
        System.out.println(orderResponse.getAddress());

        assertEquals("박지민", orderResponse.getName());
        System.out.println(orderResponse.getName());

        assertEquals(OrderStatus.PENDING, orderResponse.getStatus());
        System.out.println(orderResponse.getStatus());

        // 검증: 주문 상세 정보
        OrderDetailDto orderDetailResponse = resDto.getOrderDetailDto();

        //대체 왜 null값이 반환되는지 이해가 안됨
        assertEquals(1L, orderId);
        assertEquals(menuId, orderDetailResponse.getMenuid());
        assertEquals(restaurantId, orderDetailResponse.getRestaurantid());
        assertEquals(1L, orderDetailResponse.getCount());

        // Verify
        verify(userRepository, times(1)).findById(userId);
        verify(orderRepository, times(1)).save(any(Orders.class));
        verify(orderDetailRepository, times(1)).save(any(OrderDetail.class));

    }

    @Test
    void testGetOrder() {
        // Mocking data
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Orders mockOrder = new Orders();
        mockOrder.setId(1L);
        mockOrder.setUserId(user);
        mockOrder.setAddress("123 Main St");
        mockOrder.setName("John Doe");
        mockOrder.setOrderTime(LocalDateTime.of(2023, 9, 19, 14, 0));
        mockOrder.setStatus(OrderStatus.ACCEPTED);

        OrderDetail mockOrderDetail = new OrderDetail();
        mockOrderDetail.setOrdersId(mockOrder);
        mockOrderDetail.setMenuId(2L);
        mockOrderDetail.setRestaurantId(3L);
        mockOrderDetail.setCount(1L);
        mockOrderDetail.setPrice(15000L);

        // Mocking repositories
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(orderDetailRepository.findByOrdersId(mockOrder)).thenReturn(mockOrderDetail);

        // Act
        ResponseEntity<CombineDto> response = orderService.getOrder(1L);

        // Assert
        assertNotNull(response);
        CombineDto combineDto = response.getBody();
        assertNotNull(combineDto);

        // OrderResponseDto 검증
        OrderResponseDto orderDto = combineDto.getOrderResponseDto();
        assertNotNull(orderDto);
        assertEquals(1L, orderDto.getUserId());
        assertEquals("123 Main St", orderDto.getAddress());
        assertEquals("John Doe", orderDto.getName());
        assertEquals(LocalDateTime.of(2023, 9, 19, 14, 0), orderDto.getOrderTime());
        assertEquals(OrderStatus.ACCEPTED, orderDto.getStatus());

        // OrderDetailDto 검증
        OrderDetailDto detailDto = combineDto.getOrderDetailDto();
        assertNotNull(detailDto);
        assertEquals(1L, detailDto.getOrderId());
        assertEquals(2L, detailDto.getMenuid());
        assertEquals(3L, detailDto.getRestaurantid());
        assertEquals(1L, detailDto.getCount());
        assertEquals(15000L, detailDto.getPrice());

        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderDetailRepository, times(1)).findByOrdersId(any(Orders.class));
    }

    @Test
    void testUpdateOrder() {
        // Mocking data
        Long orderId = 1L;
        User mockOwner = new User();
        mockOwner.setId(1L);
        mockOwner.setRole(UserRole.OWNER); // OWNER 권한 설정

        Orders mockOrder = new Orders();
        mockOrder.setId(orderId);
        mockOrder.setUserId(mockOwner);  // 주문자는 OWNER 역할의 유저

        // Mocking repositories
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // Act
        OrderStatus updatedStatus = orderService.updateOrder(orderId, OrderStatus.COMPLETED);

        // Assert
        assertEquals(OrderStatus.COMPLETED, updatedStatus);  // 상태가 올바르게 업데이트되는지 확인
        verify(orderRepository, times(1)).save(mockOrder);  // 저장 메서드가 호출되었는지 검증
    }


}
