package com.sparta.delivery.entity;

import jakarta.persistence.*;

public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order orderId;

    @Column(name="menu_id")
    private Long menuId;

    @Column
    private Long count;
}
