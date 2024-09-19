package com.sparta.delivery.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // 리뷰가 작성된 주문

    @Column(nullable = false)
    private Integer rating; // 별점 (1~5)

    @Column(nullable = false)
    private String comment; // 리뷰 내용

    @Column(nullable = false)
    private LocalDateTime reviewTime; // 리뷰 작성 시각
}
