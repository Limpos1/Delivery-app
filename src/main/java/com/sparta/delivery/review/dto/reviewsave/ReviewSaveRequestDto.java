package com.sparta.delivery.review.dto.reviewsave;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSaveRequestDto {

    private int rating;
    private String content;
}
