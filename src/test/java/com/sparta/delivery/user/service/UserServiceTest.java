package com.sparta.delivery.user.service;

import com.sparta.delivery.etc.config.PasswordEncoder;
import com.sparta.delivery.etc.exception.DuplicateEmailException;
import com.sparta.delivery.etc.exception.WrongPasswordException;
import com.sparta.delivery.user.dto.SignoutRequestDto;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.dto.SignupResponseDto;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.enums.UserStatus;
import com.sparta.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void 유저생성_성공() {

        // Given
        SignupRequestDto requestDto = new SignupRequestDto("test@example.com", "Password123!", UserRole.USER);
        String encodedPassword = "encodedPassword";
        given(passwordEncoder.encode(requestDto.getPassword())).willReturn(encodedPassword);
        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        SignupResponseDto responseDto = userService.createUser(requestDto);

        // Then
        assertNotNull(responseDto);
        assertEquals(requestDto.getEmail(), responseDto.getEmail());
        then(userRepository).should().save(any(User.class));
    }

    @Test
    void 유저생성_실패__이메일_중복() {
        // Given
        SignupRequestDto requestDto = new SignupRequestDto("test@example.com", "Password123!", UserRole.USER);
        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(new User()));

        // When & Then
        assertThrows(DuplicateEmailException.class, () -> userService.createUser(requestDto));
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    public void 유저_삭제성공() {

        // Given
        Long userId = 1L;
        String inputPassword = "Password123!";
        String encodedPassword = "encodedPassword";

        // 기존 생성자 대신 SignupRequestDto를 활용해 User 인스턴스화
        SignupRequestDto requestDto = new SignupRequestDto("test@example.com", "Password123!", UserRole.USER);
        User user = new User(requestDto, encodedPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(inputPassword, user.getPassword())).willReturn(true);

        // When & Then
        userService.deleteUser(userId, new SignoutRequestDto(inputPassword));

        assertEquals(user.getStatus(), UserStatus.WITHDRAWAL);
        then(userRepository).should().findById(userId);

    }

    @Test
    public void 유저_삭제실패__패스워드_불일치() {

        // Given
        Long userId = 1L;
        String inputPassword = "WrongPassword";
        String encodedPassword = "encodedPassword";

        // 기존 생성자 대신 SignupRequestDto를 활용해 User 인스턴스화
        SignupRequestDto requestDto = new SignupRequestDto("test@example.com", "Password123!", UserRole.USER);
        User user = new User(requestDto, encodedPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(inputPassword, user.getPassword())).willReturn(false);

        // When & Then
        assertThrows(WrongPasswordException.class, () -> {
            userService.deleteUser(userId, new SignoutRequestDto(inputPassword));
        });

    }

}