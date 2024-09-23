package com.sparta.delivery.review.dto.getallreview;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewAllGetResponseDto {
    private final Long id;
    private final Long orderId;
    private final Long userId;
    private final Long restaurantId;
    private final int rating;
    private final String content;
    private final LocalDateTime createdOn;

    public ReviewAllGetResponseDto(Long id, Long orderId, Long userId, Long restaurantId, int rating, String content, LocalDateTime createdOn) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.content = content;
        this.createdOn = createdOn;
    }
}
