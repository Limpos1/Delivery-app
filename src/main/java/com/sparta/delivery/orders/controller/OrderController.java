package com.sparta.delivery.orders.controller;

import com.sparta.delivery.etc.annotation.Sign;
import com.sparta.delivery.etc.common.SignUser;
import com.sparta.delivery.orders.dto.OrderRequestDto;
import com.sparta.delivery.orders.dto.CombineDto;
import com.sparta.delivery.orders.dto.OrderResponseDto;
import com.sparta.delivery.orders.service.OrderService;
import com.sparta.delivery.orders.enums.OrderStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //주문 요청 API
    @PostMapping("/requestOrders")
    // JWT 필터링을 통해 인증된 사용자의 정보를 가져오는 매개변수 추가가 필요하다. 현재는 임시로 Dto에 있는 UserId를 사용
    public ResponseEntity<CombineDto> requestOrder(@Sign SignUser user, OrderRequestDto req) {
        String inputName = user.getName();
        Long userId = user.getId();
        return orderService.requestOrder(userId, req,inputName);
    }

    // 주문 조회 API
    @GetMapping("/gets")
    public ResponseEntity<CombineDto> getOrder(long id) {
        return orderService.getOrder(id);
    }

    // 주문 상태 변경 API
    @PatchMapping("/updates")
    //user 정보도 받아야 한다.
    public OrderStatus updateOrder(@Sign SignUser user, Long orderid, OrderStatus oEnum) {
        Long userId = user.getId();
        return orderService.updateOrder(userId, orderid, oEnum);

    }

    //가게에서 주문 목록 보기 API
    @GetMapping("getorderfromrest")
    public ResponseEntity<List<OrderResponseDto>> getOrderFromRest(@Sign SignUser user, Long restaurantid) {
        Long userId = user.getId();
        return orderService.getOrderFromRest(userId, restaurantid);
    }





}
