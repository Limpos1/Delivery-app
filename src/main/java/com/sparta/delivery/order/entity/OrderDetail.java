package com.sparta.delivery.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name="restaurant_id")
    private Long restaurantId;

    @Column
    private Long count;

    public OrderDetail(Orders ordersId, Long menuId, Long restaurantId, Long count, Long price) {
        this.ordersId = ordersId;
        this.menuId = menuId;
        this.restaurantId = restaurantId;
        this.count = count;
        this.price = price;
    }

    public OrderDetail() {}
}
