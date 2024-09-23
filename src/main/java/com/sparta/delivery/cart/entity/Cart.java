package com.sparta.delivery.cart.entity;

import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Column
    private LocalDateTime lastupdated;

    public Cart(User user) {
        this.user = user;
        this.lastupdated = LocalDateTime.now();
    }

    public void addOrUpdateMenu(Menu menu, Long count){
        CartItem viewCartItem = this.cartItems.stream()
                .filter(cartItem -> cartItem.getMenu().equals(menu))
                .findFirst()
                .orElse(null);

        if(viewCartItem != null){
            viewCartItem.setCount(count);
        } else {
            CartItem cartItem = new CartItem(this, menu, count);
            this.cartItems.add(cartItem);
        }

        this.lastupdated = LocalDateTime.now();
    }

    public void removeMenu(Menu menu) {
        this.cartItems.removeIf(cartItem -> cartItem.getMenu().equals(menu));
        this.lastupdated = LocalDateTime.now();
    }

    public void clearCart(){
        this.cartItems.clear();
        this.lastupdated = LocalDateTime.now();
    }
}
