package com.sparta.delivery.restaurant.controller;


import com.sparta.delivery.etc.config.JwtUtil;
import com.sparta.delivery.restaurant.dto.RestaurantDetailResponseDto;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;
import com.sparta.delivery.restaurant.dto.RestaurantResponseDto;
import com.sparta.delivery.restaurant.service.RestaurantService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants") // 레스토랑 관련 API의 기본 경로
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final JwtUtil jwtUtil;

    public RestaurantController(RestaurantService restaurantService, JwtUtil jwtUtil) {
        this.restaurantService = restaurantService;
        this.jwtUtil = jwtUtil;
    }

    // JWT 토큰에서 유저 ID 추출
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String jwtToken = jwtUtil.substringToken(token);
        Claims claims = jwtUtil.extractClaims(jwtToken);
        return Long.parseLong(claims.getSubject());
    }

    /*
    가게 생성
    id, name, minOrderAmount, openTime, closeTime, status
     */
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> createRestaurant(@Valid @RequestBody RestaurantRequestDto restaurantRequestDto, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);

        try {
            RestaurantResponseDto responseDto = restaurantService.createRestaurant(restaurantRequestDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    // 가게 목록 조회
    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByName(
            @RequestParam(required = false) String name) {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsbyName(name);
        return ResponseEntity.ok(restaurants);
    }

    // 가게 단건 조회
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailResponseDto> getRestaurantById(
            @PathVariable Long restaurantId) {
        RestaurantDetailResponseDto restaurantDetail = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurantDetail);
    }

    // 가게 폐업 시 상태만 폐업 상태로 변경
    @PutMapping("/{restaurantId}/close")
    public ResponseEntity<RestaurantResponseDto> closeRestaurant(
            @PathVariable Long restaurantId,
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);

        try {
            RestaurantResponseDto responseDto = restaurantService.closeRestaurant(restaurantId, userId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}