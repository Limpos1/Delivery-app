package com.sparta.delivery.restaurant.entity;

import com.sparta.delivery.restaurant.enums.RestaurantStatus;
import com.sparta.delivery.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Entity
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name",nullable = false,length = 50)
    private String name; // 가게 이름

    @Column(name = "min_order_price",nullable = false)
    private Long minOrderAmount; // 최소 주문 금액

    @Column(name = "openedAt",nullable = false)
    private LocalTime openTime; // 오픈 시간

    @Column(name = "closedAt", nullable = false)
    private LocalTime closeTime; // 마감 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // 가게 소유주 (사장님)

    @Enumerated(EnumType.STRING)
    private RestaurantStatus status = RestaurantStatus.OPEN; // 가게 상태 (OPEN, CLOSED)

    protected Restaurant () {}

    public Restaurant(String name, Long minOrderAmount, LocalTime openTime, LocalTime closeTime, User owner) {
        // 최소 주문 금액 : 10,000 이상
        if (minOrderAmount < 10000) {
            throw new IllegalArgumentException("최소 주문 금액 10,000원 이상이어야 합니다.");
        }

        // 오픈, 마감 시간
        if (openTime.isAfter(closeTime)) {
            throw new IllegalArgumentException("오픈 시간은 마감 시간보다 빨라야 합니다.");
        }

        this.name = name;
        this.minOrderAmount = minOrderAmount;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.owner = owner;
    }
}