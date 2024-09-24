package com.sparta.delivery.menu.repository;


import com.sparta.delivery.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long> {
    List<Menu> findAllByIdIn(List<Long> menuId);
}
