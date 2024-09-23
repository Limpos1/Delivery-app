package com.sparta.delivery.review.repository;

import com.sparta.delivery.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReviewReository extends JpaRepository<Review, Long> {
    List<Review> findByRestaurantIdAndRatingBetween(Long restaurantId, Integer maxRating, Integer minRating);

    List<Review> findByRestaurantId(Long restaurantId);
}
