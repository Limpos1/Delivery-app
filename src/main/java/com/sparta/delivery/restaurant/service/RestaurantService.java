package com.sparta.delivery.restaurant.service;

import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.enums.MenuStatus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.dto.RestaurantDetailResponseDto;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;
import com.sparta.delivery.restaurant.dto.RestaurantResponseDto;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.enums.RestaurantStatus;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, UserRepository userRepository, MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
    }

    // 유저 찾기
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 유저가 존재하지 않습니다."));
    }

    // 가게 찾기
    private Restaurant findRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));
    }

    // 가게 소유주 찾기
    private Restaurant findRestaurantByOwner(Long restaurantId, Long ownerId) {
        User owner = findUserById(ownerId);
        return restaurantRepository.findByIdAndOwnerId(restaurantId, owner)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않거나, 사장님이 소유한 가게가 아닙니다."));
    }

    /**
     * 가게 생성 또는 수정
     * @param restaurantRequestDto 가게 ID가 있는 경우 가게정보 수정, 없는 경우 새로운 가게 생성
     * @param userId ID를 통해 사용자 권환 확인
     */
    @Transactional
    public RestaurantResponseDto createOrUpdateRestaurant(RestaurantRequestDto restaurantRequestDto, Long userId) {
        if (restaurantRequestDto.getId() != null) {
            return updateRestaurant(restaurantRequestDto, userId);
        } else {
            return createRestaurant(restaurantRequestDto, userId);
        }
    }

    // 가게 생성
    @Transactional
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto requestDto, Long userId) {
        User user = findUserById(userId);

        if (!user.getRole().equals(UserRole.OWNER)) {
            throw new IllegalArgumentException("OWNER로 가입한 유저만 가게를 생성할 수 있습니다.");
        }

        if (restaurantRepository.countByOwnerId(user) >= 3) {
            throw new IllegalArgumentException("최대 3개의 가게만 운영할 수 있습니다.");
        }

        String inputOpenTime = requestDto.getOpenTime();
        String inputCloseTime = requestDto.getCloseTime();
        LocalTime openTime = null;
        LocalTime closeTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try{
            openTime = LocalTime.parse(inputOpenTime,formatter);
            closeTime = LocalTime.parse(inputCloseTime,formatter);
        }catch(DateTimeParseException e){
            throw new IllegalArgumentException("잘못된 시간 형식입니다.");
        }


        Restaurant restaurant = new Restaurant(
                requestDto.getName(),
                requestDto.getMinOrderAmount(),
                openTime,
                closeTime,
                user,
                requestDto.getCategory()
                );

        restaurantRepository.save(restaurant);
        return new RestaurantResponseDto(restaurant);
    }

    // 가게 수정
    @Transactional
    public RestaurantResponseDto updateRestaurant(RestaurantRequestDto requestDto, Long userId) {
        Restaurant restaurant = findRestaurantByOwner(requestDto.getId(), userId);

        String inputOpenTime = requestDto.getOpenTime();
        String inputCloseTime = requestDto.getCloseTime();
        LocalTime openTime = null;
        LocalTime closeTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try{
            openTime = LocalTime.parse(inputOpenTime,formatter);
            closeTime = LocalTime.parse(inputCloseTime,formatter);
        }catch(DateTimeParseException e){
            throw new IllegalArgumentException("잘못된 시간 형식입니다.");
        }

        restaurant.updateRestaurant(
                requestDto.getName(),
                requestDto.getMinOrderAmount(),
                openTime,
                closeTime,
                requestDto.getCategory()
        );
        restaurantRepository.save(restaurant);
        return new RestaurantResponseDto(restaurant);
    }

    // 고객의 가게조회, 업종(카테고리)로 다건조회(메뉴제외)
    @Transactional
    public List<RestaurantResponseDto> getRestaurantsbyCategory(String category) {
        List<Restaurant> restaurants = restaurantRepository.findByCategoryContainingAndStatus(category, RestaurantStatus.OPEN);
        return restaurants.stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // 고객의 가게조회, 업종(카테고리)로 단건조회, 해당 가게의 메뉴포함
    @Transactional
    public RestaurantDetailResponseDto getRestaurantById(Long restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);

        if (restaurant.getStatus() != RestaurantStatus.OPEN) {
            throw new IllegalArgumentException("해당 가게는 영업 중이 아닙니다.");
        }

        List<Menu> availableMenus = menuRepository.findAllByRestaurantAndStatus(restaurant, MenuStatus.AVAILABLE);
        return new RestaurantDetailResponseDto(restaurant, availableMenus);
    }

    // 사장님의 본인 가게 조회
    @Transactional
    public List<RestaurantResponseDto> getRestaurantsByOwner(Long ownerId) {
        User user = findUserById(ownerId);
        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndStatus(user, RestaurantStatus.OPEN);
        return restaurants.stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // 가게 폐업
    @Transactional
    public RestaurantResponseDto closeRestaurant(Long restaurantId, Long ownerId) {
        Restaurant restaurant = findRestaurantByOwner(restaurantId, ownerId);
        restaurant.closeRestaurant();
        return new RestaurantResponseDto(restaurant);
    }
}