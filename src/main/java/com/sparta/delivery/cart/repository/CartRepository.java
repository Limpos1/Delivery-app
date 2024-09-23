package com.sparta.delivery.cart.repository;

import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}
