package com.sparta.delivery.restaurant.repository;

import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
