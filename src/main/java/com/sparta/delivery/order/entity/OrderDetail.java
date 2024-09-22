package com.sparta.delivery.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(name="menu_id")
    private Long menuId;

    @Column(name="price")
    private Long price;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime orderTime; // 주문 시각

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name="restaurant_id")
    private Long restaurantId;

    @Column
    private Long count;

    public OrderDetail(Orders ordersId, Long menuId, Long restaurantId, Long count, Long price,LocalDateTime orderTime) {
        this.ordersId = ordersId;
        this.menuId = menuId;
        this.restaurantId = restaurantId;
        this.count = count;
        this.price = price;
        this.orderTime = orderTime;
    }

    public OrderDetail() {}
}
