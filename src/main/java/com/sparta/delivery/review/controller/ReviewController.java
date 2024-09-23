package com.sparta.delivery.review.controller;

import com.sparta.delivery.review.dto.getallreview.ReviewAllGetResponseDto;
import com.sparta.delivery.review.dto.reviewsave.ReviewSaveRequestDto;
import com.sparta.delivery.review.dto.reviewsave.ReviewSaveResponseDto;
import com.sparta.delivery.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/orders/{orderId}/reviews")
    public ResponseEntity<ReviewSaveResponseDto> saveReview(
            @PathVariable Long orderId,
            @RequestBody ReviewSaveRequestDto reviewSaveRequestDto) {
        return ResponseEntity.ok(reviewService.saveReivew(orderId, reviewSaveRequestDto));
    }

    @GetMapping("/orders/{orderId}/reviews/{restaurantId}")
    public ResponseEntity<List<ReviewAllGetResponseDto>> getAllReview(
            @PathVariable Long restuarantId,
            @RequestParam Integer maxRating,
            @RequestParam Integer minRating) {
        return ResponseEntity.ok(reviewService.allGetReview(restuarantId, maxRating, minRating));
    }

    @DeleteMapping("/restaurants/{restaurantId}/reviews/{reivewId}")
    public void deleteReview(@PathVariable Long restuarantId, @PathVariable Long reivewId) {
        reviewService.deleteReview(restuarantId,reivewId);
    }
}

