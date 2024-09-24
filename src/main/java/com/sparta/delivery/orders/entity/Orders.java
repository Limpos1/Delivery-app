package com.sparta.delivery.orders.entity;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.sparta.delivery.orders.enums.OrderStatus;
import com.sparta.delivery.restaurant.entity.Restaurant;
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

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime orderTime; // 주문 시각

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태

    @Column
    private Long count;

    @Column(name="total_price")
    private Long totalPrice;

    public Orders(User userId, String address, String name,Restaurant restaurant,LocalDateTime orderTime, OrderStatus status,Long count, Long totalPrice) {
        this.userId=userId;
        this.address = address;
        this.name = name;
        this.restaurant = restaurant;
        this.orderTime = orderTime;
        this.status = status;
        this.count = count;
        this.totalPrice = totalPrice;
    }

    public Orders(Long totalPrice){
        this.totalPrice = totalPrice;
    }

    public Orders() {

    }

    public boolean isCompleted() {
        return status == OrderStatus.COMPLETED;
    }
}
