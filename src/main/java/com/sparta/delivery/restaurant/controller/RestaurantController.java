package com.sparta.delivery.restaurant.controller;


import com.sparta.delivery.etc.annotation.Sign;
import com.sparta.delivery.etc.common.SignUser;
import com.sparta.delivery.restaurant.dto.RestaurantDetailResponseDto;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;
import com.sparta.delivery.restaurant.dto.RestaurantResponseDto;
import com.sparta.delivery.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants") // 레스토랑 관련 API의 기본 경로
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /* 가게생성
    name, minOrderAmount, openTime, closeTime */
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> createOrUpdateRestaurant(
            @Valid @RequestBody RestaurantRequestDto restaurantRequestDto,
            @Sign SignUser signUser) {
        Long userId = signUser.getId();

        try {
            // 가게 ID가 있으면 수정, 없으면 생성
            RestaurantResponseDto responseDto;
            if (restaurantRequestDto.getId() != null) {
                // 가게 수정
                responseDto = restaurantService.updateRestaurant(restaurantRequestDto, userId);
                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                // 가게 생성
                responseDto = restaurantService.createRestaurant(restaurantRequestDto, userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
            }
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
            @Sign SignUser signUser) {
        Long userId = signUser.getId();

        try {
            RestaurantResponseDto responseDto = restaurantService.closeRestaurant(restaurantId, userId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}