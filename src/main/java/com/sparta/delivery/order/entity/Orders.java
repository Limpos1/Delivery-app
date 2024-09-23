package com.sparta.delivery.order.entity;

import com.sparta.delivery.order.enums.OrderStatus;
import com.sparta.delivery.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(nullable = false)
    private String address;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime orderTime; // 주문 시각

    @Column(nullable = false)
    private String name;

    @Column(name="restaurant_id")
    private Long restaurantId;


    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태

    public Orders(User userId, String address, String name,Long restaurantId,LocalDateTime orderTime, OrderStatus status) {
        this.userId=userId;
        this.address = address;
        this.name = name;
        this.restaurantId = restaurantId;
        this.orderTime = orderTime;
        this.status = status;
    }

    public Orders() {

    }
}
