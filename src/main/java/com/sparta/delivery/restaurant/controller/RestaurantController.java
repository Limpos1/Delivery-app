package com.sparta.delivery.restaurant.controller;


import com.sparta.delivery.etc.annotation.Sign;
import com.sparta.delivery.etc.common.SignUser;
import com.sparta.delivery.restaurant.dto.RestaurantDetailResponseDto;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;
import com.sparta.delivery.restaurant.dto.RestaurantResponseDto;
import com.sparta.delivery.restaurant.service.RestaurantService;
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

    /**
     * 가게생성
     * @param restaurantRequestDto : 가게 생성에 필요한 정보
     *                             name, minOrderAmount, openTime, closeTime, category
     *                             category: KOREAN, WESTERN, CHINESE, JAPANESE
     * @return 가게 생성 시 201 Created
     */
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> createRestaurant(
            @RequestBody RestaurantRequestDto restaurantRequestDto,
            @Sign SignUser signUser) {
        Long userId = signUser.getId();

        RestaurantResponseDto responseDto = restaurantService.createRestaurant(restaurantRequestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 가게 수정 : 가게 id 필요
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantRequestDto restaurantRequestDto,
            @Sign SignUser signUser){
        Long userId = signUser.getId();

        RestaurantResponseDto responseDto = restaurantService.updateRestaurant(id, restaurantRequestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    // 고객의 가게 다건 조회, 업종(카테고리로) 조회
    @GetMapping("/category")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByCategory(
            @RequestParam String category) {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsbyCategory(category);
        return ResponseEntity.ok(restaurants);
    }

    // 고객의 가게조회, 업종(카테고리)로 단건조회, 해당 가게의 메뉴포함
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailResponseDto> getRestaurantById(
            @PathVariable Long restaurantId) {
        RestaurantDetailResponseDto restaurantDetail = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurantDetail);
    }

    // 사장님의 본인 가게 조회
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByOwner(
            @PathVariable Long ownerId) {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsByOwner(ownerId);
        return ResponseEntity.ok(restaurants);
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