package com.sparta.delivery.menu.service;

import com.sparta.delivery.menu.dto.*;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.enums.MenuStatus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.restaurantRepository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    //메뉴 생성
    public MenuSaveResponseDto saveMenu(MenuSaveRequestDto menuSaveRequestDto) {
        //사용자 정보 확인
        User user = userRepository.findById(menuSaveRequestDto.getUserId()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        //사용자가 사장님인지 확인
        if (user.getRole() != UserRole.OWNER) {
            throw new IllegalArgumentException("메뉴등록 권한이 없습니다.");
        }
        //사용자가 등록하려는 가게가 본인 가게인지 확인
        //RestaurantDto에서 가게ID로 실제 restaurant 엔티티 조회
        Restaurant restaurant = restaurantRepository.findById(menuSaveRequestDto.getRestaurantDto().getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
        if (!restaurant.getOwnerId().equals(user)) {
            throw new IllegalArgumentException("본인의 가게인지 확인하세요");
        }

        Menu menu = new Menu(
                menuSaveRequestDto.getName(),
                menuSaveRequestDto.getPrice(),
                restaurant,
                MenuStatus.AVAILABLE
        );
        Menu savedMenu = menuRepository.save(menu);

//        RestaurantDto restaurantDto = new RestaurantDto();
        return new MenuSaveResponseDto(
                savedMenu.getName(),
                savedMenu.getPrice(),
                menuSaveRequestDto.getRestaurantDto(),
                savedMenu.getId()
        );
    }

    //메뉴 수정
    public MenuUpdateResponseDto updateMenu(Long menuId, MenuUpdateRequestDto menuUpdateRequestDto) {
        //사용자 정보 확인
        User user = userRepository.findById(menuUpdateRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        //사장님인지 고객인지 분류
        if (user.getRole() != UserRole.OWNER) {
            throw new IllegalArgumentException("메뉴를 수정할 권한이 없습니다.");
        }

        //수정할 메뉴 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 메뉴를 찾을 수 없습니다."));

        //이 메뉴가 본인 가게 메뉴인지 확인
        Restaurant restaurant = restaurantRepository.findById(menuUpdateRequestDto.getRestaurantDto().getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (!restaurant.getOwnerId().equals(user)) {
            throw new IllegalArgumentException("본인 가게인지 확인하세요.");
        }

        //메뉴 정보 업데이트(수정)
        menu.update(menuUpdateRequestDto.getName(),
                menuUpdateRequestDto.getPrice());

        Menu updatedMenu = menuRepository.save(menu);

        return new MenuUpdateResponseDto(
                updatedMenu.getId(),
                updatedMenu.getName(),
                updatedMenu.getPrice(),
                menuUpdateRequestDto.getRestaurantDto()

        );


    }
}
