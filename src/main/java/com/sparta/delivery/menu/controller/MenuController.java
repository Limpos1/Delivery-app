package com.sparta.delivery.menu.controller;


import com.sparta.delivery.etc.annotation.Sign;
import com.sparta.delivery.etc.common.SignUser;
import com.sparta.delivery.menu.dto.*;
import com.sparta.delivery.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("restaurant/menu")
public class MenuController {

    private final MenuService menuService;

    //메뉴 생성
    @PostMapping("/save")
    public ResponseEntity<MenuSaveResponseDto> saveMenu(@Sign SignUser signUser, @RequestBody MenuSaveRequestDto menuSaveRequestDto) {
        MenuSaveResponseDto response = menuService.saveMenu(signUser,menuSaveRequestDto);
        return ResponseEntity.ok(response);
    }

    //메뉴 수정
    @PatchMapping("/{menuId}")
    public ResponseEntity<MenuUpdateResponseDto> updateMenu(@Sign SignUser signUser,@PathVariable Long menuId, @RequestBody MenuUpdateRequestDto menuUpdateRequestDto){
        MenuUpdateResponseDto response = menuService.updateMenu(signUser,menuId,menuUpdateRequestDto);
        return ResponseEntity.ok(response);
    }

    //메뉴 삭제
    @DeleteMapping("/{menuId}")
    public ResponseEntity<String> deleteMenu(@Sign SignUser signUser,@PathVariable Long menuId, @RequestBody MenuDeleteRequestDto menuDeleteRequestDto){
        menuService.deleteMenu(signUser,menuId,menuDeleteRequestDto);
        return ResponseEntity.ok("메뉴가 삭제되었습니다.");

    }
}
