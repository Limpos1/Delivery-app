package com.sparta.delivery.restaurant.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    // @Mock : 테스트에서 사용할 의존성(레포지토리나 서비스 클래스)을 목(mock)으로 설정.
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MenuRepository menuRepository;
    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    public void restaurant_생성_성공() {
        // given: 의존성 설정
        Long userId = 1L;
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Test Restaurant",
                10000L,
                LocalTime.parse("10:00"),
                LocalTime.parse("22:00")
        );

        // 유저 생성 및 열할 설정
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.OWNER);

        // 가게 객체 생성
        Restaurant restaurant = new Restaurant(
                "Test Restaurant",
                10000L,
                LocalTime.parse("10:00"),
                LocalTime.parse("22:00"),
                user);

        // 가짜 데이터 설정
        given(userRepository.findById(userId)).willReturn(Optional.of(user)); // 유저 찾기
        given(restaurantRepository.countByOwnerId(user)).willReturn(0L); // 가게 수
        given(restaurantRepository.save(any(Restaurant.class))).willReturn(restaurant); // 가게 저장

        // when : 가게 생성 메서드 호출
        RestaurantResponseDto result = restaurantService.createRestaurant(requestDto, userId);

        // then : 결과 검증
        assertNotNull(result);
        assertEquals("Test Restaurant", result.getName()); // 가게 이름 검증
        assertEquals(10000L, result.getMinOrderAmount()); // 최소 주문 금액 검증
    }

    @Test
    public void 가게_다건_조회_성공() {
        // given : 가게 객체 생성
        Restaurant restaurant = new Restaurant(
                "Test Restaurant",
                10000L,
                LocalTime.parse("10:00"),
                LocalTime.parse("22:00"),
                new User()
        );

        given(restaurantRepository.findByNameContainingAndStatus("Test", RestaurantStatus.OPEN))
                .willReturn(Collections.singletonList(restaurant)); // 가게 목록 설정

        // when : 가게 다건 조회 메서드 호출
        List<RestaurantResponseDto> result = restaurantService.getRestaurantsbyName("Test");

        // then : 결과 검증
        assertNotNull(result);
        assertEquals(1, result.size()); // 결과 개수 검증
        assertEquals("Test Restaurant", result.get(0).getName()); // 가게 이름 검증
    }

    @Test
    public void 가게_단건_조회_성공() {
        // given : 단건 조회 가게 객체 생성
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant(
                "Test Restaurant",
                10000L,
                LocalTime.parse("10:00"),
                LocalTime.parse("22:00"),
                new User()
        );

        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant)); // 가게 조회
        given(menuRepository.findAll()).willReturn(Collections.emptyList()); // 메뉴가 없다고 가정

        // when : 가게 단건 조회 메서드 호출
        RestaurantDetailResponseDto result = restaurantService.getRestaurantById(restaurantId);

        // then : 결과 검증
        assertNotNull(result);
        assertEquals("Test Restaurant", result.getName()); // 가게 이름 검증
        assertTrue(result.getMenus().isEmpty()); // 메뉴가 없으면 빈 리스트여야 함
    }

    @Test
    public void 가게_폐업_성공() {
        // given : 폐업 테스트 객체 생성
        Long restaurantId = 1L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);
        owner.setRole(UserRole.OWNER);

        Restaurant restaurant = new Restaurant(
                "Test Restaurant",
                10000L,
                LocalTime.parse("10:00"),
                LocalTime.parse("22:00"),
                owner
        );

        given(userRepository.findById(ownerId)).willReturn(Optional.of(owner)); // 소유자 조회
        given(restaurantRepository.findByIdAndOwnerId(restaurantId, owner)).willReturn(Optional.of(restaurant)); // 가게 조회

        // when : 가게 폐업 메서드 호출
        RestaurantResponseDto result = restaurantService.closeRestaurant(restaurantId, ownerId);

        // then : 결과 검증
        assertNotNull(result);
        assertEquals("Test Restaurant", result.getName()); // 가게 이름 검증
        assertEquals(RestaurantStatus.CLOSED, restaurant.getStatus()); // 상태가 변경되었는지 확인
    }
}