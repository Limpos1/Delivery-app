package com.sparta.delivery.menu.controller;

import com.sparta.delivery.menu.dto.MenuSaveRequestDto;
import com.sparta.delivery.menu.dto.MenuSaveResponseDto;
import com.sparta.delivery.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    //메뉴 생성
    @PostMapping("/save")
    public ResponseEntity<MenuSaveResponseDto> saveMenu(@RequestBody MenuSaveRequestDto menuSaveRequestDto) {
        MenuSaveResponseDto response = menuService.saveMenu(menuSaveRequestDto);
        return ResponseEntity.ok(response);
    }

}
