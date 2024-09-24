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

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 유저가 존재하지 않습니다."));
    } // 유저 찾기

    private Restaurant findRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));
    } // 가게 찾기

    private Restaurant findRestaurantByOwner(Long restaurantId, Long ownerId) {
        User owner = findUserById(ownerId);
        return restaurantRepository.findByIdAndOwnerId(restaurantId, owner)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않거나, 사장님이 소유한 가게가 아닙니다."));
    }  // 가게 소유주 찾기

    @Transactional // 가게생성
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto requestDto, Long userId) {
        User user = findUserById(userId);

        if (!user.getRole().equals(UserRole.OWNER)) {
            throw new IllegalArgumentException("OWNER로 가입한 유저만 가게를 생성할 수 있습니다.");
        } // 사장님 권한 확인

        if (restaurantRepository.countByOwnerId(user) >= 3) {
            throw new IllegalArgumentException("최대 3개의 가게만 운영할 수 있습니다.");
        } // 사장님은는 최대 3개의 가게만 운영할 수 있다

        Restaurant restaurant = new Restaurant(
                requestDto.getName(),
                requestDto.getMinOrderAmount(),
                requestDto.getOpenTime(),
                requestDto.getCloseTime(),
                user);

        restaurantRepository.save(restaurant);
        return new RestaurantResponseDto(restaurant);
    }

    @Transactional // 가게수정
    public RestaurantResponseDto updateRestaurant(RestaurantRequestDto requestDto, Long userId) {
        Restaurant restaurant = findRestaurantByOwner(requestDto.getId(), userId);

        restaurant.updateRestaurant(
                requestDto.getName(),
                requestDto.getMinOrderAmount(),
                requestDto.getOpenTime(),
                requestDto.getCloseTime()
        );
        restaurantRepository.save(restaurant);
        return new RestaurantResponseDto(restaurant);
    }

    @Transactional // 가게조회, 다건조회(메뉴제외)
    public List<RestaurantResponseDto> getRestaurantsbyName(String name) {
        List<Restaurant> restaurants = restaurantRepository.findByNameContainingAndStatus(name, RestaurantStatus.OPEN);
        return restaurants.stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional // 가게조회, 단건조회(메뉴포함)
    public RestaurantDetailResponseDto getRestaurantById(Long restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);

        if (restaurant.getStatus() != RestaurantStatus.OPEN) {
            return null;
        }

        List<Menu> availableMenus = menuRepository.findAll().stream()
                .filter(menu -> menu.getRestaurant().getId().equals(restaurantId) && menu.getStatus() == MenuStatus.AVAILABLE)
                .collect(Collectors.toList());
        return new RestaurantDetailResponseDto(restaurant, availableMenus);
    }

    @Transactional // 가게폐업
    public RestaurantResponseDto closeRestaurant(Long restaurantId, Long ownerId) {
        Restaurant restaurant = findRestaurantByOwner(restaurantId, ownerId);
        restaurant.closeRestaurant();
        return new RestaurantResponseDto(restaurant);
    }
}