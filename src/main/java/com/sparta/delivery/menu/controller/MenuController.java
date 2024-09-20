package com.sparta.delivery.menu.controller;

import com.sparta.delivery.menu.dto.*;
import com.sparta.delivery.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //메뉴 수정
    @PatchMapping("/{menuId}")
    public ResponseEntity<MenuUpdateResponseDto> updateMenu(@PathVariable Long menuId, @RequestBody MenuUpdateRequestDto menuUpdateRequestDto){
        MenuUpdateResponseDto response = menuService.updateMenu(menuId,menuUpdateRequestDto);
        return ResponseEntity.ok(response);
    }

    //메뉴 삭제
    @DeleteMapping("/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable Long menuId, @RequestBody MenuDeleteRequestDto menuDeleteRequestDto){
        menuService.deleteMenu(menuId,menuDeleteRequestDto);
        return ResponseEntity.ok("메뉴가 삭제되었습니다.");

    }
}
