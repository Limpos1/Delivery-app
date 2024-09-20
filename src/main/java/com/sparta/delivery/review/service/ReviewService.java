package com.sparta.delivery.review.service;

import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.orders.repository.OrderRepository;
import com.sparta.delivery.review.dto.getallreview.ReviewAllGetResponseDto;
import com.sparta.delivery.review.dto.reviewsave.ReviewSaveRequestDto;
import com.sparta.delivery.review.dto.reviewsave.ReviewSaveResponseDto;
import com.sparta.delivery.review.entity.Review;
import com.sparta.delivery.review.filter.ReviewFilter;
import com.sparta.delivery.review.repository.ReviewReository;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewReository reviewRepository;
    private final ReviewFilter reviewFilter;
    private final OrderRepository orderRepository;

    @Transactional
    public ReviewSaveResponseDto saveReivew(Long orderId, ReviewSaveRequestDto reviewSaveRequestDto) {
        Orders order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("Order not found"));

        if (!order.isCompleted()) {
            throw new IllegalArgumentException("Order is not completed");
        }

        Review review = new Review(order.getRestaurant(), reviewSaveRequestDto.getRating(), reviewSaveRequestDto.getContent());
        Review savedReview = reviewRepository.save(review);

        return new ReviewSaveResponseDto(
                savedReview.getId(),
                order.getId(),
                order.getUser().getId(),
                order.getRestaurant().getId(),
                savedReview.getRating(),
                savedReview.getComment(),
                savedReview.getCreatedOn());
    }

    public List<ReviewAllGetResponseDto> allGetReview(Long restuarantId, Integer maxRating, Integer minRating) {
        List<Review> reviews = reviewFilter.filterReviews(restuarantId, minRating, maxRating);
        List<ReviewAllGetResponseDto> dtoList = reviews.stream()
                .map(review -> new ReviewAllGetResponseDto(
                        review.getId(),
                        review.getOrder().getId(),
                        review.getUser().getId(),
                        review.getRestaurant().getId(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedOn()))
                .collect(Collectors.toList());

        return dtoList;
    }


    @Transactional
    public void deleteReview(Long restuarantId, Long reivewId) {
        Review review = reviewRepository.findById(reivewId).orElseThrow(
                () -> new IllegalArgumentException("Review not found"));

        if(!review.getRestaurant().getId().equals(restuarantId)) {
            throw new IllegalArgumentException("Restaurant id does not match");
        }
        reviewRepository.deleteById(reivewId);
    }
}

