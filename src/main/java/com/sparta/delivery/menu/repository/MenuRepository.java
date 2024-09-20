package com.sparta.delivery.menu.repository;


import com.sparta.delivery.menu.entity.Menus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menus, Long> {
    List<Menus> findAllByMenuList(List<Menus> menuList);
}
