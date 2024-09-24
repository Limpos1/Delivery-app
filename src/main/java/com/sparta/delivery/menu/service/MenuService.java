package com.sparta.delivery.menu.service;


import com.sparta.delivery.etc.common.SignUser;
import com.sparta.delivery.menu.dto.*;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.enums.MenuStatus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    //메뉴 생성
    public MenuSaveResponseDto saveMenu(SignUser signUser, MenuSaveRequestDto menuSaveRequestDto) {
        //사용자 정보 확인
        User user = userRepository.findById(signUser.getId()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//menuSaveRequestDto.getUserId()
        //사용자가 사장님인지 확인
        if (user.getRole() != UserRole.OWNER) {
            throw new IllegalArgumentException("메뉴등록 권한이 없습니다.");
        }
        //사용자가 등록하려는 가게가 본인 가게인지 확인
        //RestaurantDto에서 가게ID로 실제 restaurant 엔티티 조회
        Restaurant restaurant = restaurantRepository.findById(menuSaveRequestDto.getRestaurantId())
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


        RestaurantDto restaurantDto = new RestaurantDto(restaurant);
        return new MenuSaveResponseDto(
                savedMenu.getName(),
                savedMenu.getPrice(),
                restaurantDto,
                savedMenu.getId()
        );
    }

    //메뉴 수정
    public MenuUpdateResponseDto updateMenu(SignUser signUser, Long menuId, MenuUpdateRequestDto menuUpdateRequestDto) {
        //사용자 정보 확인
        User user = userRepository.findById(signUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//menuUpdateRequestDto.getUserId()
        //사장님인지 고객인지 분류
        if (user.getRole() != UserRole.OWNER) {
            throw new IllegalArgumentException("메뉴를 수정할 권한이 없습니다.");
        }

        //수정할 메뉴 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 메뉴를 찾을 수 없습니다."));

        //사용자가 소유한 모든 가게 목록 조회
        List<Restaurant> restaurants = restaurantRepository.findAllByOwnerId_Id(user.getId());

        if (restaurants.isEmpty()) {
            throw new IllegalArgumentException("소유한 가게가 없습니다.");
        }

        //메뉴가 사용자의 특정 가게에 해당하는지 확인
        boolean ownerRestaurant = restaurants.stream()
                .anyMatch(restaurant -> restaurant.getId().equals(menuUpdateRequestDto.getRestaurantId()));
        if(!ownerRestaurant) {
            throw new IllegalArgumentException("본인 가게의 메뉴만 수정할 수 있습니다. 원하는 가게가 맞는지 확인하세요.");
        }


        //메뉴 정보 업데이트(수정)
        menu.update(menuUpdateRequestDto.getName(),
                menuUpdateRequestDto.getPrice());

        Menu updatedMenu = menuRepository.save(menu);

        Restaurant restaurant = restaurantRepository.findById(menuUpdateRequestDto.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        RestaurantDto restaurantDto = new RestaurantDto(restaurant);
        return new MenuUpdateResponseDto(
                updatedMenu.getId(),
                updatedMenu.getName(),
                updatedMenu.getPrice(),
                restaurantDto

        );


    }

    //메뉴 삭제
    public void deleteMenu(SignUser signUser,Long menuId, MenuDeleteRequestDto menuDeleteRequestDto) {
        //사용자 정보 확인
        User user = userRepository.findById(signUser.getId())
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//menuDeleteRequestDto.getUserId()
        //사장님인지 확인
        if (user.getRole() != UserRole.OWNER){
            throw new IllegalArgumentException("메뉴를 삭제할 권한이 없습니다.");
        }

        //삭제할 메뉴 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()-> new IllegalArgumentException("삭제할 메뉴를 찾을 수 없습니다."));

        //사용자가 소유한 가게 목록 조회
        List<Restaurant> restaurants = restaurantRepository.findAllByOwnerId_Id(user.getId());

        if (restaurants.isEmpty()) {
            throw new IllegalArgumentException("소유한 가게가 없습니다.");
        }

        //메뉴가 사용자의 특정 가게에 해당하는지 확인
        boolean ownerRestaurant = restaurants.stream()
                .anyMatch(restaurant -> restaurant.getId().equals(menuDeleteRequestDto.getRestaurantId()));
        if(!ownerRestaurant) {
            throw new IllegalArgumentException("본인 가게의 메뉴만 수정할 수 있습니다. 원하는 가게가 맞는지 확인하세요.");
        }


        //메뉴 상태를 enum의 'DELETE'로 변경
        menu.updateStatus(MenuStatus.DELETED);

        menuRepository.save(menu);

    }
}
