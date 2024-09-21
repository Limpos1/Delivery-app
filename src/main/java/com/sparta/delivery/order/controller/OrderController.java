package com.sparta.delivery.order.controller;

import com.sparta.delivery.order.dto.OrderRequestDto;
import com.sparta.delivery.order.entity.CombineDto;
import com.sparta.delivery.order.enums.OrderStatus;
import com.sparta.delivery.order.service.OrderService;
import com.sparta.delivery.user.enums.UserRole;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //주문 요청 API
    @PostMapping("/orderrequest")
    // JWT 필터링을 통해 인증된 사용자의 정보를 가져오는 매개변수 추가가 필요하다. 현재는 임시로 Dto에 있는 UserId를 사용
    public ResponseEntity<CombineDto> orderrequest(OrderRequestDto req) {
        return orderService.orderrequest(req);
    }

    // 주문 조회 API
    @GetMapping("/get")
    public ResponseEntity<CombineDto> getOrder(long id) {
        return orderService.getOrder(id);
    }

    @PatchMapping
    //user 정보도 받아야 한다.
    public OrderStatus updateOrder(Long orderid, OrderStatus oEnum) {
        return orderService.updateOrder(orderid, oEnum);
    }

    // 주문 상태 변경 API





}
