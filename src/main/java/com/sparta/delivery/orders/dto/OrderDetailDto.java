package com.sparta.delivery.orders.dto;

import com.sparta.delivery.cart.entity.CartItem;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDetailDto {
    Long orderId;
    Long menuid;
    String menuName;
    Long restaurantid;
    Long price;
    LocalDateTime ordertime;

    public OrderDetailDto(Long orderid, Long menuid,String menuName, Long restaurantid, Long price, LocalDateTime ordertime) {
        this.orderId = orderid;
        this.menuid = menuid;
        this.menuName = menuName;
        this.restaurantid = restaurantid;
        this.price = price;
        this.ordertime = ordertime;
    }
}
