package com.sparta.delivery.menu.entity;
import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.menu.enums.MenuStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100)
    private String name; // 메뉴 이름

    @Column(nullable = false)
    private int price; // 메뉴 가격

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart; // 해당 메뉴가 속한 카트 (카트와의 관계 설정)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Restaurant restaurant; // 해당 메뉴가 속한 가게

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MenuStatus status; // 메뉴 상태 (AVAILABLE, DELETED)

    public Menu(String name, int price, Restaurant restaurant, MenuStatus status) {
        this.name = name;
        this.price = price;
        this.restaurant = restaurant;
        this.status = status;
    }
}
