package com.sparta.delivery.orders.entity;

import com.sparta.delivery.orders.enums.OrderStatus;
import com.sparta.delivery.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime orderTime; // 주문 시각

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태
}
