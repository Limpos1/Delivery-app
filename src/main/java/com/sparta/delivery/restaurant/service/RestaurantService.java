package com.sparta.delivery.restaurant.service;

import com.sparta.delivery.exception.NoSignedUserException;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;
import com.sparta.delivery.restaurant.dto.RestaurantResponseDto;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    // 가게 생성
    @Transactional
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto requestDto, Long userId) {

        // 유저 찾기
        User user =  userRepository.findById(userId).orElseThrow(() -> new NoSignedUserException());

        // 사장님 권한 확인
        if (!user.getRole().equals(UserRole.OWNER)) {
            throw new IllegalArgumentException("OWNER로 가입한 유저만 가게를 생성할 수 있습니다.");
        }

        // 사장님은는 최대 3개의 가게만 운영할 수 있다
        if (restaurantRepository.countByOwnerId(userId) >= 3) {
            throw new IllegalArgumentException("최대 3개의 가게만 운영할 수 있습니다.");
        }

        // 가게 생성(조건은 생성자에서 처리)
        Restaurant restaurant = new Restaurant(
                requestDto.getName(),
                requestDto.getMinOrderAmount(),
                requestDto.getOpenTime(),
                requestDto.getCloseTime(),
                user);

        restaurantRepository.save(restaurant);
        return new RestaurantResponseDto(restaurant);
    }



    // 가게 폐업
    @Transactional
    public RestaurantResponseDto closeRestaurant(Long restaurantId, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findByIdAndOwner_Id(restaurantId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 존재하기 않습니다."));

        restaurant.closeRestaurant();
        return new RestaurantResponseDto(restaurant);
    }
}