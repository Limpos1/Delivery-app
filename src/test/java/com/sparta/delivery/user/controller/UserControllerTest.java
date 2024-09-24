package com.sparta.delivery.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sparta.delivery.etc.common.SignUser;
import com.sparta.delivery.etc.config.PasswordEncoder;
import com.sparta.delivery.etc.exception.DuplicateEmailException;
import com.sparta.delivery.etc.exception.GlobalExceptionHandler;
import com.sparta.delivery.etc.exception.WrongPasswordException;
import com.sparta.delivery.user.dto.SignoutRequestDto;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.dto.SignupResponseDto;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    // 회원가입 테스트
    @Test
    void 회원가입_성공() throws Exception {
        // Given
        SignupRequestDto signupRequestDto = new SignupRequestDto("test@example.com", "Password123!", "name", UserRole.USER);
        SignupResponseDto signupResponseDto = new SignupResponseDto(1L, "test@example.com", "name", LocalDateTime.now(), LocalDateTime.now());

        // Mocking
        given(userService.createUser(any(SignupRequestDto.class))).willReturn(signupResponseDto);

        // When & Then
        mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(signupRequestDto.getEmail()));
    }

    @Test
    void 회원가입_실패__중복이메일() throws Exception {
        // Given
        SignupRequestDto signupRequestDto = new SignupRequestDto("duplicate@example.com", "Password123!", "name", UserRole.USER);

        // Mocking: 중복 이메일 예외 발생
        given(userService.createUser(any(SignupRequestDto.class))).willThrow(new DuplicateEmailException());

        // When & Then
        mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isBadRequest());
    }

    // 회원탈퇴 테스트
    @Test
    void 회원탈퇴_성공() throws Exception {
        // Given
        SignoutRequestDto signoutRequestDto = new SignoutRequestDto("Password123!");

        // Mocking
        BDDMockito.willDoNothing().given(userService).deleteUser(any(Long.class), any(SignoutRequestDto.class));

        // When & Then
        mockMvc.perform(delete("/api/signout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signoutRequestDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void 회원탈퇴_실패__틀린_비밀번호() throws Exception {
        // Given

        // Mock SignUser (로그인된 사용자)
        SignUser signUser = new SignUser(1L, "test@example.com", "name");
        SignoutRequestDto signoutRequestDto = new SignoutRequestDto("WrongPassword!");

        // PasswordEncoder의 matches 메서드가 false를 반환하도록 모킹
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

        // Mocking: 비밀번호 불일치 예외 발생
        doThrow(new WrongPasswordException()).when(userService).deleteUser(any(Long.class), any(SignoutRequestDto.class));

        // When & Then
        mockMvc.perform(delete("/api/signout")
                        .requestAttr("userId", signUser.getId()) // SignUser 주입
                        .requestAttr("email", signUser.getEmail()) // SignUser 주입
                        .requestAttr("name", signUser.getName()) // SignUser 주입
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signoutRequestDto)))
                .andExpect(status().isBadRequest());  // 400 Bad Request 기대
    }
}
