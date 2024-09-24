package com.sparta.delivery.user.repository;

import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 이메일로_유저_조회_성공() {

        // Given
        String encodedPassword = "encodedPassword";
        // 기존 생성자 대신 SignupRequestDto를 활용해 User 인스턴스화
        SignupRequestDto requestDto = new SignupRequestDto("test@example.com", "Password123!", "name", UserRole.USER);
        User user = new User(requestDto, encodedPassword);
        ReflectionTestUtils.setField(user, "id", 1L);
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void 이메일로_유저_조회_실패__존재하지_않는_이메일() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(foundUser.isPresent());
    }
}