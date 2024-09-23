package com.sparta.delivery.review.service;

import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.review.dto.reviewsave.ReviewSaveRequestDto;
import com.sparta.delivery.review.dto.reviewsave.ReviewSaveResponseDto;
import com.sparta.delivery.review.entity.Review;
import com.sparta.delivery.review.filter.ReviewFilter;
import com.sparta.delivery.review.repository.ReviewReository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewServiceTest {


    @Mock
    private ReviewReository reviewRepository;

    @Mock
    private ReviewFilter reviewFilter;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Orders order;

    @BeforeEach
    void setUp() {
        order = new Orders();
        ReflectionTestUtils.setField(order, "id", 1L);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    }

    @Test
    void saveReviewTest(){
        ReviewSaveRequestDto request = new ReviewSaveRequestDto();
        request.setRating(5);
        request.setContent("review");

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review(order.getRestaurant(),5,"review"));

        ReviewSaveResponseDto response = reviewService.saveReivew(1L, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(5, response.getRating());
        Assertions.assertEquals("review", response.getContent());



    }
}