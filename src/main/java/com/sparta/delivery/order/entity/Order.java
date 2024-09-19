package com.sparta.delivery.order.entity;

import com.sparta.delivery.restorant.entity.Restaurant;
import com.sparta.delivery.enums.OrderStatus;
import com.sparta.delivery.menu.entity.Menu;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu; // 주문된 메뉴

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant; // 메뉴가 속한 가게

    @Column(nullable = false)
    private LocalDateTime orderTime; // 주문 시각

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태
}
