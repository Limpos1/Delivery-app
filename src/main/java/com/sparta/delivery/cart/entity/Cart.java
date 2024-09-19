package com.sparta.delivery.cart.entity;

import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter

@NoArgsConstructor
public class Cart{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //

    @Column(name = "menu_id")
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<Menu> menus = new ArrayList<>();

    @Column
    private Long count;

    @Column
    private LocalDateTime lastUpdated;

    public Cart(User user, Long count) {
        this.user = user;
        this.count = count;
        this.lastUpdated = LocalDateTime.now();
    }

    public void addMenu(Menu menu) {
        menus.add(menu);
        menu.serCart(this);
    }

    public void removeMenu(Menu menu) {
        menus.remove(menu);
        menu.serCart(null);
    }

    public void clearCart() {
        this.menus.clear();
        this.count = 0L;
    }


}

