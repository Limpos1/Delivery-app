package com.sparta.delivery.cart.entity;



import com.sparta.delivery.menu.entity.Menus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@Entity
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menus menu;

    private Long count;

    public CartItem(Cart cart, Menus menu, Long count) {
        this.cart = cart;
        this.menu = menu;
        this.count = count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
