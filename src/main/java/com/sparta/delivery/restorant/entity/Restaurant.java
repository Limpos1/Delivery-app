package com.sparta.delivery.restorant.entity;

import com.sparta.delivery.enums.RestaurantStatus;
import com.sparta.delivery.enums.UserRole;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 가게 이름

    @Column(nullable = false)
    private int minOrderAmount; // 최소 주문 금액

    @Column(nullable = false)
    private LocalTime openTime; // 오픈 시간

    @Column(nullable = false)
    private LocalTime closeTime; // 마감 시간

    //테이블 관계 설정필요
    @Column(nullable = false)
    private UserRole owner; // 가게 소유주 (사장님)

    @Enumerated(EnumType.STRING)
    private RestaurantStatus status = RestaurantStatus.OPEN; // 가게 상태 (OPEN, CLOSED)
}
