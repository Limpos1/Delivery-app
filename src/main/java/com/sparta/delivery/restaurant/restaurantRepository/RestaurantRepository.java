package com.sparta.delivery.restaurant.restaurantRepository;

import com.sparta.delivery.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
