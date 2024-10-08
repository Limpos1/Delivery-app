package com.sparta.delivery.restaurant.repository;

import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.enums.RestaurantStatus;
import com.sparta.delivery.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    long countByOwnerId(User ownerId);

    Optional<Restaurant> findByIdAndOwnerId(Long restaurantId, User ownerId);

    List<Restaurant> findByNameContainingAndStatus(String name, RestaurantStatus status);

    List<Restaurant> findAllByOwnerId_Id(Long ownerId);
    List<Restaurant> findByCategoryContainingAndStatus(String category, RestaurantStatus status);
    List<Restaurant> findByOwnerIdAndStatus(User ownerId, RestaurantStatus status);
}