package com.sparta.delivery.review.dto.reviewsave;

import com.sparta.delivery.restaurant.entity.Restaurant;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewSaveResponseDto {

    private final Long id;
    private final Long orderId;
    private final Long userId;
    private final Restaurant restaurantId;
    private final int rating;
    private final String content;
    private final LocalDateTime createdOn;

    public ReviewSaveResponseDto(Long id, Long orderId, Long userId, Restaurant restaurantId, int rating, String content, LocalDateTime createdOn) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.content = content;
        this.createdOn = createdOn;
    }
}
