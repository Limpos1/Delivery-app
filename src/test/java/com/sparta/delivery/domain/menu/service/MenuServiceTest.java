package com.sparta.delivery.domain.menu.service;

import com.sparta.delivery.etc.common.SignUser;
import com.sparta.delivery.menu.dto.*;
import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.enums.MenuStatus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.menu.service.MenuService;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

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
        SignUser signUser = new SignUser(userId, "test@naver.com");
        RestaurantDto restaurantDto = new RestaurantDto(restaurantId);
        MenuSaveRequestDto menuSaveRequestDto = new MenuSaveRequestDto(1L, "chicken", 20000, 2L);

        //사장님 역할 설정
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.OWNER);

        //가게 주인 설정
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(user);

        Menu menu = new Menu("chicken", 20000, restaurant, MenuStatus.AVAILABLE);


        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuRepository.save(any(Menu.class))).willReturn(menu);

        //when
        MenuSaveResponseDto result = menuService.saveMenu(signUser, menuSaveRequestDto);

        //then
        assertNotNull(result);
        assertEquals("chicken", result.getName());
        assertEquals(20000, result.getPrice());
        assertEquals(restaurantId, result.getRestaurantDto().getStoreId());


    }

    @Test
    public void menu_수정_성공() {
        //given
        Long userId = 1L;
        Long menuId = 1L;
        Long restaurantId = 3L;

        SignUser signUser = new SignUser(userId, "test@naver.com");

        //user랑 restaurant mock
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.OWNER);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(user);

        //기존메뉴(old)정보
        Menu existMenu = new Menu("Old", 15000, restaurant, MenuStatus.AVAILABLE);

        //수정요청 dto
        MenuUpdateRequestDto menuUpdateRequestDto = new MenuUpdateRequestDto(1L, 1L, "New", 18000, new RestaurantDto(3L));

        given(menuRepository.findById(menuId)).willReturn(Optional.of(existMenu));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuRepository.save(any(Menu.class))).willReturn(existMenu);
        //when
        MenuUpdateResponseDto result = menuService.updateMenu(signUser, menuId, menuUpdateRequestDto);

        //then
        assertNotNull(result);
        assertEquals("New", result.getName());
        assertEquals(18000, result.getPrice());
        assertEquals(restaurantId, result.getRestaurantDto().getStoreId());
    }

    @Test
    public void menu_수정_실패_메뉴없음() {

        //given
        Long userId = 1L;
        Long menuId = 1L;
        SignUser signUser = new SignUser(userId, "test@naver.com");

        MenuUpdateRequestDto menuUpdateRequestDto = new MenuUpdateRequestDto(1L, menuId, "Updated Menu", 20000, new RestaurantDto(2L));

        //사장
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.OWNER);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(menuRepository.findById(menuId)).willReturn(Optional.empty());

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.updateMenu(signUser, menuId, menuUpdateRequestDto);
        });
        System.out.println("예외발생 - "+ exception.getMessage());

    }


    @Test
    void 메뉴_수정_실패_유저없음(){

        //given
        Long userId = 1L;
        Long menuId = 1L;
        SignUser signUser = new SignUser(userId, "test@naver.com");

        MenuUpdateRequestDto menuUpdateRequestDto = new MenuUpdateRequestDto(1L,1L,"New",12000,new RestaurantDto(1L));

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->menuService.updateMenu(signUser,menuId,menuUpdateRequestDto));
        System.out.println("예외발생 - "+ exception.getMessage());
    }

    @Test
    void 메뉴_수정_실패_사장이아님(){

        //given
        Long userId = 1L;
        Long menuId = 1L;
        SignUser signUser = new SignUser(userId, "test@naver.com");
        //고객유저
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.USER);

        MenuUpdateRequestDto menuUpdateRequestDto = new MenuUpdateRequestDto(userId,menuId,"Old",12000,new RestaurantDto(1L));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->menuService.updateMenu(signUser,menuId,menuUpdateRequestDto));
        System.out.println("예외발생 - "+ exception.getMessage());
    }

    @Test
    void 메뉴_수정_실패_식당없음(){
        //given
        Long userId = 1L;
        Long menuId = 1L;
        Long restaurantId = 2L;

        SignUser signUser = new SignUser(userId, "test@naver.com");

        //사장
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.OWNER);

        Menu menu = new Menu("Old",10000,null,MenuStatus.AVAILABLE);

        MenuUpdateRequestDto menuUpdateRequestDto = new MenuUpdateRequestDto(userId,menuId,"New",12000,new RestaurantDto(restaurantId));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->menuService.updateMenu(signUser,menuId,menuUpdateRequestDto));
        System.out.println("예외발생 - "+ exception.getMessage());
    }

    @Test
    void 메뉴_수정_실패_본인가게_아님(){
        //given
        Long userId = 1L;
        Long menuId = 1L;
        Long restaurantId = 2L;
        SignUser signUser = new SignUser(userId, "test@naver.com");

        //가게주인
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.OWNER);

        //다른가게 주인
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setRole(UserRole.OWNER);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(anotherUser);

        Menu menu = new Menu("Old",10000,restaurant,MenuStatus.AVAILABLE);

        MenuUpdateRequestDto menuUpdateRequestDto = new MenuUpdateRequestDto(userId,menuId,"New",12000,new RestaurantDto(restaurantId));

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->menuService.updateMenu(signUser,menuId,menuUpdateRequestDto));
        System.out.println("예외발생 - "+ exception.getMessage());
    }



    @Test
    void 메뉴_삭제_성공() {
        //given
        Long menuId = 1L;
        Long userId = 1L;
        Long restaurantId = 2L;
        SignUser signUser = new SignUser(userId, "test@naver.com");

        MenuDeleteRequestDto menuDeleteRequestDto = new MenuDeleteRequestDto(userId, new RestaurantDto(restaurantId));

        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.OWNER);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(user);

        Menu menu = new Menu("Sample Menu", 10000, restaurant, MenuStatus.AVAILABLE);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));

        //when
        menuService.deleteMenu(signUser, menuId, menuDeleteRequestDto);

        //then

        verify(menuRepository).save(any(Menu.class));
        assertEquals(MenuStatus.DELETED, menu.getStatus());


    }

}
