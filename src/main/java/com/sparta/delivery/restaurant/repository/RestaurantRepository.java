package com.sparta.delivery.restaurant.repository;

import com.sparta.delivery.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    long countByOwnerId(Long ownerId);
}