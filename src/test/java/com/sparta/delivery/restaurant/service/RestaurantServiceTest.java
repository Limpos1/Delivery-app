package com.sparta.delivery.restaurant.service;

import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.enums.MenuStatus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.dto.RestaurantDetailResponseDto;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;
import com.sparta.delivery.restaurant.dto.RestaurantResponseDto;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.enums.RestaurantCategory;
import com.sparta.delivery.restaurant.enums.RestaurantStatus;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MenuRepository menuRepository;
    @InjectMocks
    private RestaurantService restaurantService;

    // 가게생성 성공 테스트
    @Test
    void 가게생성_성공() {
        // given: 의존성 설정
        Long userId = 1L;
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "test@naver.com",
                "encodedPassword",
                "Test User",
                UserRole.OWNER
        );
        User owner = new User(signupRequestDto, "encodedPassword");

        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Test Restaurant",
                RestaurantCategory.KOREAN,
                10000L,
                "10:00",
                "22:00");

        given(userRepository.findById(userId)).willReturn(Optional.of(owner)); // 유저 찾기
        given(restaurantRepository.countByOwnerId(owner)).willReturn(1L); // 가게 수
        given(restaurantRepository.save(any(Restaurant.class))).willReturn(new Restaurant());

        // when : 가게 생성 메서드 호출
        RestaurantResponseDto responseDto = restaurantService.createRestaurant(requestDto, userId);

        // then : 결과 검증
        assertNotNull(responseDto);
        assertEquals("Test Restaurant", responseDto.getName()); // 가게 이름 검증
        then(restaurantRepository).should().save(any(Restaurant.class)); // 최소 주문 금액 검증
    }

    // 가게생성 실패 테스트
    @Test
    void 가게생성_실패_유저_권한없음() {
        // given
        Long userId = 1L;
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "testuser@gmail.com",
                "encodedPassword",
                "Test User",
                UserRole.USER
        );
        User nonOwner = new User(signupRequestDto, "encodedPassword");

        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Test User Restaurant",
                RestaurantCategory.WESTERN,
                30000L,
                "09:00",
                "19:00");

        given(userRepository.findById(userId)).willReturn(Optional.of(nonOwner));

        // When&Then
        assertThrows(IllegalArgumentException.class, () -> restaurantService.createRestaurant(requestDto, userId));
        then(restaurantRepository).should(never()).save(any(Restaurant.class));
    }

    // 가게수정 성공 테스트
    @Test
    void 가게수정_성공() {
        // given
        Long restaurantId = 1L;
        Long userId = 1L;
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "test@naver.com",
                "encodedPassword",
                "Test Owner",
                UserRole.OWNER);

        User owner = new User(signupRequestDto, "encodedPassword");

        Restaurant restaurant = new Restaurant(
                "Owner Restaurant",
                RestaurantCategory.CHINESE,
                80000L,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                owner
        );

        RestaurantRequestDto updateRequestDto = new RestaurantRequestDto(
                "Update Restaurant", // 이 이름을 기대합니다
                RestaurantCategory.KOREAN,
                120000L,
                "10:00",
                "16:00"
        );

        given(restaurantRepository.findByIdAndOwnerId(restaurantId, owner)).willReturn(Optional.of(restaurant));
        given(userRepository.findById(userId)).willReturn(Optional.of(owner));

        // When
        RestaurantResponseDto responseDto = restaurantService.updateRestaurant(restaurantId, updateRequestDto, userId);

        // Then
        assertNotNull(responseDto);
        assertEquals(updateRequestDto.getName(), responseDto.getName()); // 수정된 이름 검증
        then(restaurantRepository).should().save(restaurant); // restaurant가 저장되었는지 검증
    }

    // 유저가게조회 (업종으로 다건조회) 테스트
    @Test
    void 유저가게조회_성공_업종다건조회() {
        // given : 가게 객체 생성
        String category = "KOREAN";
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "test@naver.com",
                "encodedPassword",
                "Test Owner",
                UserRole.USER);

        User user = new User(signupRequestDto, "encodedPassword");

        Restaurant restaurant1 = new Restaurant("Restaurant 1", RestaurantCategory.KOREAN, 10000L, LocalTime.of(10, 0), LocalTime.of(22, 0), user);
        Restaurant restaurant2 = new Restaurant("Restaurant 2", RestaurantCategory.KOREAN, 20000L, LocalTime.of(9, 0), LocalTime.of(19, 0), user);

        List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);
        given(restaurantRepository.findByCategoryContainingAndStatus(category, RestaurantStatus.OPEN)).willReturn(restaurants);

        // when
        List<RestaurantResponseDto> responseDtos = restaurantService.getRestaurantsbyCategory(category);

        // then
        assertNotNull(responseDtos);
        assertEquals(2, responseDtos.size()); // 가게 수 검증
        assertEquals("Restaurant 1", responseDtos.get(0).getName());
        assertEquals("Restaurant 2", responseDtos.get(1).getName());
    }

    // 유저가게조회 (단건조회 메뉴포함) 테스트
    @Test
    void 가게조회_성공_단건() {
        // given
        Long restaurantId = 1L;
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "test@naver.com",
                "encodedPassword",
                "Test Owner",
                UserRole.USER
        );
        User user = new User(signupRequestDto, "encodedPassword");

        Restaurant restaurant = new Restaurant("Restaurant", RestaurantCategory.KOREAN, 10000L, LocalTime.of(10, 0), LocalTime.of(22, 0), user);
        restaurant.setStatus(RestaurantStatus.OPEN);

        Menu menu = new Menu("Menu Item", 15000, restaurant, MenuStatus.AVAILABLE);

        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuRepository.findAllByRestaurantAndStatus(restaurant, MenuStatus.AVAILABLE)).willReturn(Arrays.asList(menu));

        // when
        RestaurantDetailResponseDto responseDto = restaurantService.getRestaurantById(restaurantId);

        // then
        assertNotNull(responseDto);
        assertEquals("Restaurant", responseDto.getName());
        assertEquals(1, responseDto.getMenus().size()); // 메뉴 수 검증
        assertEquals("Menu Item", responseDto.getMenus().get(0).getName()); // 메뉴 이름 검증
    }

    // 가게조회 실패 테스트
    @Test
    void 가게조회_실패_영업중아님() {
        // given
        Long restaurantId = 1L;
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "test@naver.com",
                "encodedPassword",
                "Test Owner",
                UserRole.USER
        );
        User user = new User(signupRequestDto, "encodedPassword");

        Restaurant restaurant = new Restaurant("Closed Restaurant", RestaurantCategory.KOREAN, 10000L, LocalTime.of(10, 0), LocalTime.of(22, 0), user);
        restaurant.closeRestaurant(); // 가게 폐업 처리

        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        assertThrows(IllegalArgumentException.class, () -> restaurantService.getRestaurantById(restaurantId));
    }

    // 가게폐업 테스트
    @Test
    void 가게폐업_성공() {
        // given: 의존성 설정
        Long restaurantId = 1L;
        Long ownerId = 1L;
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "owner@naver.com",
                "encodedPassword",
                "Test Owner",
                UserRole.OWNER
        );

        User owner = new User(signupRequestDto, "encodedPassword") {
            @Override
            public Long getId() {
                return ownerId; // ownerId를 직접 반환
            }
        };

        Restaurant restaurant = new Restaurant("Owner Restaurant", RestaurantCategory.CHINESE, 80000L, LocalTime.of(9, 0), LocalTime.of(22, 0), owner);
        given(userRepository.findById(ownerId)).willReturn(Optional.of(owner)); // 사용자 Mocking 추가
        given(restaurantRepository.findByIdAndOwnerId(restaurantId, owner)).willReturn(Optional.of(restaurant));

        // when: 가게 폐업 메서드 호출
        RestaurantResponseDto responseDto = restaurantService.closeRestaurant(restaurantId, ownerId);

        // then: 결과 검증
        assertNotNull(responseDto);
        assertEquals("Owner Restaurant", responseDto.getName()); // 가게 이름 검증
        assertEquals(RestaurantStatus.CLOSED, restaurant.getStatus()); // 상태가 CLOSED로 변경되었는지 검증
        then(restaurantRepository).should().findByIdAndOwnerId(restaurantId, owner); // 레포지토리 호출 검증
    }
}