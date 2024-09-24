package com.sparta.delivery.menu.repository;


import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.enums.MenuStatus;
import com.sparta.delivery.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long> {
    List<Menu> findAllByIdIn(List<Long> menuId);

    // 특정 레스토랑에 속한 AVAILABLE 상태의 메뉴를 조회할 때 사용
    List<Menu> findAllByRestaurantAndStatus(Restaurant restaurant, MenuStatus status);
}