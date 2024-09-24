package com.sparta.delivery.orders.aop;

import com.sparta.delivery.orders.dto.CombineDto;
import com.sparta.delivery.orders.dto.OrderDetailDto;
import com.sparta.delivery.orders.repository.OrderDetailRepository;
import com.sparta.delivery.orders.repository.OrdersRepository;
import com.sparta.delivery.orders.entity.OrderDetail;
import com.sparta.delivery.orders.enums.OrderStatus;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Aspect
@Component
public class OrderAoP {
    private static final Logger logger = LoggerFactory.getLogger(OrderAoP.class);
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    OrdersRepository ordersRepository;

    // 주문 생성 메서드를 위한 Pointcut
    @Pointcut("execution(* com.sparta.delivery.orders.service.OrderService.orderrequest(..))")
    public void orderRequestPointcut() {}

    // 주문 상태 업데이트 메서드를 위한 Pointcut
    @Pointcut("execution(* com.sparta.delivery.orders.service.OrderService.updateOrder(Long, Long, com.sparta.delivery.orders.enums.OrderStatus)) && args(userId, orderId, oEnum)")
    public void updateOrderPointcut(Long userId, Long orderId, OrderStatus oEnum) {}

    // 주문 생성 시 로그 남기기
    @AfterReturning(value = "orderRequestPointcut()", returning = "response")
    public void logOrderRequest(Object response) {
        if (response instanceof ResponseEntity) {
            ResponseEntity<?> res = (ResponseEntity<?>) response;
            if (res.getBody() instanceof CombineDto) {
                CombineDto combineDto = (CombineDto) res.getBody();
                OrderDetailDto detailDto = combineDto.getOrderDetailDto();
                LocalDateTime now = LocalDateTime.now();
                Long restaurantId = detailDto.getRestaurantid();
                Long orderId = detailDto.getOrderId();
                logger.info("New Order: 요청 시각={}, 가게 ID={}, 주문 ID={}", now, restaurantId, orderId);
            }
        }
    }

    // 주문 상태 업데이트 시 로그 남기기
    @AfterReturning(value = "updateOrderPointcut(userId, orderId, oEnum)", returning = "status")
    public void logUpdateOrder(JoinPoint joinPoint, Long userId, Long orderId, OrderStatus oEnum, OrderStatus status) {
        Long restaurantId = getRestaurantIdByOrderId(orderId); // 주문 ID로 식당 ID를 얻는 메서드
        LocalDateTime now = LocalDateTime.now();
        logger.info("Order Update: 요청 시각={}, 가게 ID={}, 주문 ID={}, 새로운 상태={}", now, restaurantId, orderId, status);
    }

    // 주문 ID로 레스토랑 ID를 얻는 메서드 (레포지토리 사용)
    private Long getRestaurantIdByOrderId(Long orderId) {
        OrderDetail orderDetail = orderDetailRepository.findByOrdersId(ordersRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found")));
        return orderDetail.getRestaurantId();
    }
}
