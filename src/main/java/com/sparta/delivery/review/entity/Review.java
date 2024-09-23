package com.sparta.delivery.review.entity;

import com.sparta.delivery.order.entity.Orders;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@Entity
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private int rating; // 별점 (1~5)

    @Column(nullable = false,length = 100)
    private String comment; // 리뷰 내용

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdOn; // 리뷰 작성 시각

    public Review(Restaurant restaurant, int rating, String content) {
        this.restaurant = restaurant;
        this.rating = rating;
        this.comment = content;
        this.createdOn = LocalDateTime.now();
    }

    public Orders getOrder() {
        return orderId;
    }

    public User getUser() {
        return userId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }
}
