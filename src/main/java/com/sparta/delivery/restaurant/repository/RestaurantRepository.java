package com.sparta.delivery.restaurant.repository;

import com.sparta.delivery.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    long countByOwnerId(Long ownerId);

    Optional<Restaurant> findByIdAndOwner_Id(Long id, Long ownerID);
}