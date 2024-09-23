package com.sparta.delivery.restaurant.service;

import com.sparta.delivery.exception.NoSignedUserException;
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
        if (restaurantRepository.countByOwnerId(user) >= 3) {
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

     // 가게 조회, 다건 조회(메뉴 제외)
    @Transactional
    public List<RestaurantResponseDto> getRestaurantsbyName(String name) {
        List<Restaurant> restaurants = restaurantRepository.findByNameContainingAndStatus(name, RestaurantStatus.OPEN);
        return restaurants.stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // 가게 조회, 단건 조회(메뉴 포함)
    @Transactional
    public RestaurantDetailResponseDto getRestaurantById(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));

        List<Menu> allMenus = menuRepository.findAll();
        List<Menu> availableMenus = allMenus.stream()
                .filter(menu -> menu.getRestaurant().getId().equals(restaurantId) && menu.getStatus() == MenuStatus.AVAILABLE)
                .collect(Collectors.toList());

        return new RestaurantDetailResponseDto(restaurant, availableMenus);
    }

    // 가게 폐업
    @Transactional
    public RestaurantResponseDto closeRestaurant(Long restaurantId, Long ownerId) {
        // ownerId로 User 찾기
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        // restaurantId와 ownerId로 가게 찾기
        Restaurant restaurant = restaurantRepository.findByIdAndOwnerId(restaurantId, owner)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 존재하기 않습니다."));

        restaurant.closeRestaurant();
        return new RestaurantResponseDto(restaurant);
    }
}