package com.sparta.delivery.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; //

    @Column(name ="menu_id")
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<Menu> menuId;

    @Column
    private Long count;

}
