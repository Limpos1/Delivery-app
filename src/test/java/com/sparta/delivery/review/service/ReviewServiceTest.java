package com.sparta.delivery.review.service;

import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.orders.enums.OrderStatus;
import com.sparta.delivery.orders.repository.OrdersRepository;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.review.dto.getallreview.ReviewAllGetResponseDto;
import com.sparta.delivery.review.dto.reviewsave.ReviewSaveRequestDto;
import com.sparta.delivery.review.dto.reviewsave.ReviewSaveResponseDto;
import com.sparta.delivery.review.entity.Review;
import com.sparta.delivery.review.filter.ReviewFilter;
import com.sparta.delivery.review.repository.ReviewReository;
import com.sparta.delivery.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ReviewServiceTest {


    @Mock
    private ReviewReository reviewRepository;

    @Mock
    private ReviewFilter reviewFilter;

    @Mock
    private OrdersRepository orderRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Orders order;
    private User user;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        restaurant = new Restaurant();
        ReflectionTestUtils.setField(restaurant, "id", 1L);

        order = new Orders();
        ReflectionTestUtils.setField(order, "id", 1L);
        order.setUserId(user);
        order.setRestaurantId(restaurant);
        order.setStatus(OrderStatus.COMPLETED);
    }

    @Test
    void saveReviewTest() {
        ReviewSaveRequestDto request = new ReviewSaveRequestDto();
        request.setRating(5);
        request.setContent("review");

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review(restaurant, 5, "review"));

        ReviewSaveResponseDto response = reviewService.saveReivew(1L, request);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("review", response.getContent());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void getReviewTest() {
        Review review1 = new Review(restaurant, 5, "Good");
        ReflectionTestUtils.setField(review1, "orderId", order);
        ReflectionTestUtils.setField(review1, "userId", user);

        Review review2 = new Review(restaurant, 4, "Nice");
        ReflectionTestUtils.setField(review2, "orderId", order);
        ReflectionTestUtils.setField(review2, "userId", user);

        when(reviewFilter.filterReviews(anyLong(), anyInt(), anyInt())).thenReturn(List.of(review1, review2));

        List<ReviewAllGetResponseDto> response = reviewService.allGetReview(1L, 5, 1);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(5, response.get(0).getRating());
        verify(reviewFilter, times(1)).filterReviews(anyLong(), anyInt(), anyInt());
    }

    @Test
    void deleteReviewTest() {
        Review review = new Review(restaurant, 5, "review");
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L, 1L);

        verify(reviewRepository, times(1)).deleteById(anyLong());
    }
}