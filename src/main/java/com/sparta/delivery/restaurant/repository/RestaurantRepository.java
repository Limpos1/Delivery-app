package com.sparta.delivery.restaurant.repository;

import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.enums.RestaurantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    long countByOwnerId(Long ownerId);

    Optional<Restaurant> findByIdAndOwner_Id(Long restaurantId, Long ownerId);

    List<Restaurant> findOpenRestaurantByName(String name, RestaurantStatus status);
}