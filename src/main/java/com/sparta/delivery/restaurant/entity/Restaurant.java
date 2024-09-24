package com.sparta.delivery.restaurant.entity;

import com.sparta.delivery.restaurant.enums.RestaurantCategory;
import com.sparta.delivery.restaurant.enums.RestaurantStatus;
import com.sparta.delivery.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name", nullable = false, length = 50)
    private String name; // 가게 이름

    @Column(name = "min_order_price", nullable = false)
    private Long minOrderAmount; // 최소 주문 금액

    //오후에 열고 새벽에 닫으면 오류가 발생. 따라서 LocalTime을 LocalTime으로 변경함.
    @Column(name = "openedAt", nullable = false)
    private LocalTime openTime; // 오픈 시간

    //오후에 열고 새벽에 닫으면 오류가 발생. 따라서 LocalTime을 LocalTime으로 변경함.
    @Column(name = "closedAt", nullable = false)
    private LocalTime closeTime; // 마감 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User ownerId; // 가게 소유주 (사장님)

    @Enumerated(EnumType.STRING)
    private RestaurantStatus status = RestaurantStatus.OPEN; // 가게상태(OPEN, CLOSED)

    @Enumerated(EnumType.STRING)
    private RestaurantCategory category; // 가게업종(KOREAN, WESTERN, CHINESE, JAPANESE)

    // 가게 생성
    public Restaurant(
            String name, Long minOrderAmount, LocalTime openTime, LocalTime closeTime, User owner, RestaurantCategory category) {
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
        this.ownerId = owner;
        this.category = category;
    }

    // 가게 수정, 정보 업데이트
    public void updateRestaurant(
            String name, Long minOrderAmount, LocalTime openTime, LocalTime closeTime, RestaurantCategory category) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (minOrderAmount != null && minOrderAmount >= 10000) {
            this.minOrderAmount = minOrderAmount;
        }
        if (openTime != null) {
            this.openTime = openTime;
        }
        if (closeTime != null) {
            this.closeTime = closeTime;
        }
        if (category != null) {
            this.category = category;
        }
    }

    // 가게 폐업, 가게 상태를 CLOSED로 변경
    public void closeRestaurant() {
        this.status = RestaurantStatus.CLOSED;
    }
}