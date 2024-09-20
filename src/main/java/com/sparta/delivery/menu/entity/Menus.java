package com.sparta.delivery.menu.entity;

import com.sparta.delivery.cart.entity.CartItem;
import com.sparta.delivery.menu.enums.MenuStatus;
import com.sparta.delivery.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Menus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100)
    private String name; // 메뉴 이름

    @Column(nullable = false)
    private int price; // 메뉴 가격

    @OneToMany(mappedBy = "menu") // 해당 메뉴가 속한 카트 (카트와의 관계 설정)
    private List<CartItem> cartItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Restaurant restaurant; // 해당 메뉴가 속한 가게

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MenuStatus status; // 메뉴 상태 (AVAILABLE, DELETED)

    public Menus(Restaurant restaurant, String name, int price, MenuStatus status) {
        this.restaurant = restaurant;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
