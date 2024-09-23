package com.sparta.delivery.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.delivery.auth.dto.LoginRequestDto;
import com.sparta.delivery.auth.service.AuthService;
import com.sparta.delivery.config.JwtUtil;
import com.sparta.delivery.config.PasswordEncoder;
import com.sparta.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@MockBean(JpaMetamodelMappingContext.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;  // AuthService를 모킹

    @MockBean
    private UserRepository userRepository;  // UserRepository 모킹

    @MockBean
    private PasswordEncoder passwordEncoder;  // PasswordEncoder 모킹

    @MockBean
    private JwtUtil jwtUtil;  // JwtUtil 모킹

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 로그인_성공() throws Exception {
        // Given
        String email = "test@example.com";
        String password = "Password123!";
        LoginRequestDto requestDto = new LoginRequestDto(email, password);

        // AuthService의 login 메서드를 모킹
        given(authService.login(any(LoginRequestDto.class))).willReturn("mocked_jwt_token");

        // When
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(header().string("Authorization", "mocked_jwt_token"));
    }

    @Test
    void 로그인_실패__이메일_없음() throws Exception {
        // Given: 잘못된 요청 (이메일 필드 없음)
        String invalidRequestJson = "{ \"password\": \"Password123!\" }";

        // When
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson));

        // Then: 400 Bad Request 반환
        resultActions.andExpect(status().isBadRequest());
    }
}
