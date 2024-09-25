package com.sparta.delivery.auth.service;

import com.sparta.delivery.etc.auth.dto.LoginRequestDto;
import com.sparta.delivery.etc.auth.service.AuthService;
import com.sparta.delivery.etc.config.JwtUtil;
import com.sparta.delivery.etc.config.PasswordEncoder;
import com.sparta.delivery.etc.exception.NoSignedUserException;
import com.sparta.delivery.etc.exception.WrongPasswordException;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.enums.UserStatus;
import com.sparta.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void 로그인_성공() {
        // Given
        String email = "test@example.com";
        String password = "Password123!";
        String encodedPassword = "encodedPassword";
        LoginRequestDto requestDto = new LoginRequestDto(email, password);

        // 기존 생성자 대신 SignupRequestDto를 활용해 User 인스턴스화
        SignupRequestDto signupRequestDto = new SignupRequestDto("test@example.com", "Password123!", "name", UserRole.USER);
        User user = new User(signupRequestDto, encodedPassword);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);
        given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getName())).willReturn("mocked_jwt_token");

        // When
        String token = authService.login(requestDto);

        // Then
        assertNotNull(token);
        assertEquals("mocked_jwt_token", token);
        then(userRepository).should().findByEmail(email);
        then(passwordEncoder).should().matches(password, user.getPassword());
        then(jwtUtil).should().createToken(user.getId(), user.getEmail(), user.getName());
    }

    @Test
    void 로그인_실패__존재하지_않는_이메일() {
        // Given
        String email = "nonexistent@example.com";
        String password = "Password123!";
        LoginRequestDto requestDto = new LoginRequestDto(email, password);

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // When & Then
        assertThrows(NoSignedUserException.class, () -> authService.login(requestDto));
        then(userRepository).should().findByEmail(email);
        then(passwordEncoder).should(never()).matches(anyString(), anyString());
        then(jwtUtil).should(never()).createToken(anyLong(), anyString(), anyString());
    }

    @Test
    void 로그인_실패__잘못된_비밀번호() {
        // Given
        String email = "test@example.com";
        String password = "WrongPassword!";
        String encodedPassword = "encodedPassword";
        LoginRequestDto requestDto = new LoginRequestDto(email, password);

        // 기존 생성자 대신 SignupRequestDto를 활용해 User 인스턴스화
        SignupRequestDto signupRequestDto = new SignupRequestDto("test@example.com", "Password123!", "name", UserRole.USER);
        User user = new User(signupRequestDto, encodedPassword);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(false);

        // When & Then
        assertThrows(WrongPasswordException.class, () -> authService.login(requestDto));
        then(userRepository).should().findByEmail(email);
        then(passwordEncoder).should().matches(password, user.getPassword());
        then(jwtUtil).should(never()).createToken(anyLong(), anyString(), anyString());
    }

    @Test
    void 로그인_실패__탈퇴한_유저() {
        // Given
        String email = "withdrawn@example.com";
        String password = "Password123!";
        String encodedPassword = "encodedPassword";
        LoginRequestDto requestDto = new LoginRequestDto(email, password);

        // 기존 생성자 대신 SignupRequestDto를 활용해 User 인스턴스화
        SignupRequestDto signupRequestDto = new SignupRequestDto(email, "Password123!", "name", UserRole.USER);
        User user = new User(signupRequestDto, encodedPassword);
        ReflectionTestUtils.setField(user, "status", UserStatus.WITHDRAWAL);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);

        // When & Then
        assertThrows(NoSignedUserException.class, () -> authService.login(requestDto));
        then(userRepository).should().findByEmail(email);
        then(passwordEncoder).should().matches(password, user.getPassword());
        then(jwtUtil).should(never()).createToken(anyLong(), anyString(), anyString());
    }
}
