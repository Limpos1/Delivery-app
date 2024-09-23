package com.sparta.delivery.review.filter;

import com.sparta.delivery.review.entity.Review;
import com.sparta.delivery.review.repository.ReviewReository;
import com.sparta.delivery.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewFilter {

    private final ReviewReository reviewReository;

    public List<Review> filterReviews(Long restaurantId, Integer maxRating, Integer minRating){
        if(maxRating != null && minRating != null){
            return reviewReository.findByRestaurantIdAndRatingBetween(restaurantId,maxRating,minRating);
        } else {
            return reviewReository.findByRestaurantId(restaurantId);
        }
    }
}
