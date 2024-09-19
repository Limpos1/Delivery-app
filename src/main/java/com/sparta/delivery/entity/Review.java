package com.sparta.delivery.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Restaurant storeId;

    @Column(nullable = false)
    private Integer rating; // 별점 (1~5)

    @Column(nullable = false,length = 100)
    private String comment; // 리뷰 내용

    @Column(name = "created_at", nullable = false)
    private LocalDateTime reviewTime; // 리뷰 작성 시각
}
