package com.sparta.delivery.user.controller;

import com.sparta.delivery.etc.annotation.Sign;
import com.sparta.delivery.etc.common.SignUser;
import com.sparta.delivery.user.dto.SignoutRequestDto;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.dto.SignupResponseDto;
import com.sparta.delivery.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /***
     * 회원가입
     * @param signupRequestDto : 가입에 필요한 이메일, 비밀번호,권한(USER 또는 OWNER)
     * @return 코드 201
     */
    @PostMapping("/api/signup")
    public ResponseEntity<SignupResponseDto> createUser(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(signupRequestDto));

    }

    /***
     * 회원탈퇴
     * @param signUser : 로그인 정보
     * @param signoutRequestDto : 탈퇴시 본인확인용 비밀번호
     * @return 코드 204
     */
    @DeleteMapping("/api/signout")
    public ResponseEntity<Long> deleteUser(@Sign SignUser signUser, @RequestBody SignoutRequestDto signoutRequestDto) {
        Long id = signUser.getId();
        userService.deleteUser(id, signoutRequestDto);
        return ResponseEntity.noContent().build();
    }

}
