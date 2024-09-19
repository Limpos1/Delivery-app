package com.sparta.delivery.menu.entity;
import com.sparta.delivery.restorant.entity.Restaurant;
import com.sparta.delivery.enums.MenuStatus;
import jakarta.persistence.*;

@Entity
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 메뉴 이름

    @Column(nullable = false)
    private int price; // 메뉴 가격

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant; // 해당 메뉴가 속한 가게

    @Enumerated(EnumType.STRING)
    private MenuStatus status; // 메뉴 상태 (AVAILABLE, DELETED)
}
