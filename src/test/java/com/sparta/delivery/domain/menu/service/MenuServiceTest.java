package com.sparta.delivery.domain.menu.service;

import com.sparta.delivery.menu.dto.MenuSaveRequestDto;
import com.sparta.delivery.menu.dto.MenuSaveResponseDto;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.enums.MenuStatus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.menu.service.MenuService;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.restaurantRepository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.userRepository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @InjectMocks
    private MenuService menuService;

    @Test
    public void menu_생성_성공() {
        //given
        Long userId = 1L;
        Long restaurantId = 2L;
        MenuSaveRequestDto menuSaveRequestDto = new MenuSaveRequestDto(1L,"chicken",20000,2L);

        //System.out.println(menuSaveRequestDto.getUserId());


           //사장님 역할 설정
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.OWNER);

           //가게 주인 설정
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(user);

        Menu menu = new Menu("chicken",20000,restaurant,MenuStatus.AVAILABLE);


        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuRepository.save(any(Menu.class))).willReturn(menu);

        //when
        MenuSaveResponseDto result = menuService.saveMenu(menuSaveRequestDto);
        //then
        assertNotNull(result);
        assertEquals("chicken",result.getName());
        assertEquals(20000,result.getPrice());
        assertEquals(restaurantId,result.getRestaurantDto().getStoreId());


    }

}
