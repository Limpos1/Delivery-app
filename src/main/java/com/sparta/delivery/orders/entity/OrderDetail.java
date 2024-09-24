package com.sparta.delivery.orders.entity;

import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.entity.CartItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders ordersId;

    @Column(name = "menu_id")
    private Long menuId;

    @Column(name="menu_name")
    private String menuName;

    @Column(name="price")
    private int price;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime orderTime; // 주문 시각

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name="restaurant_id")
    private Long restaurantId;



    public OrderDetail(Orders ordersId, Long menuId,String menuName, Long restaurantId, int price,LocalDateTime orderTime) {
        this.ordersId = ordersId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.restaurantId = restaurantId;
        this.price = price;
        this.orderTime = orderTime;
    }

    public OrderDetail() {}
}
