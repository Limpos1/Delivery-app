package com.sparta.delivery.cart.util;

import com.sparta.delivery.menu.entity.Menu;
import com.sparta.delivery.menu.repository.MenuRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FindRestaurantUtil {

    public static List<Menu> findRestuarant(List<Long> menuIds, MenuRepository menuRepository) {
        return menuIds.stream()
                .map(menuId -> menuRepository.findById(menuId)
                        .orElseThrow(() -> new IllegalArgumentException("menu not found")))
                .toList();
    }
}