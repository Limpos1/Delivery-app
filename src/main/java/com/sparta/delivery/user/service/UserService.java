package com.sparta.delivery.user.service;

import com.sparta.delivery.etc.config.PasswordEncoder;
import com.sparta.delivery.etc.exception.DuplicateEmailException;
import com.sparta.delivery.etc.exception.WrongPasswordException;
import com.sparta.delivery.user.dto.SignoutRequestDto;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.dto.SignupResponseDto;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponseDto createUser(SignupRequestDto requestDto) {

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        //User Entity의 email 중복예외처리
        Optional<User> existingUser = userRepository.findByEmail(requestDto.getEmail());
        if (existingUser.isPresent()) {
            throw new DuplicateEmailException();
        }
        // RequestDto -> entity
        User user = new User(requestDto, encodedPassword);
        // DB저장
        User savedUser = userRepository.save(user);
        // Entity -> ResponseDto
        return new SignupResponseDto(savedUser);

    }

    @Transactional
    public void deleteUser(Long id, SignoutRequestDto signoutRequestDto) {
        //id에 맞는 유저찾기
        User user = userRepository.findById(id).orElseThrow();
        //패스워드가 일치한다면
        if (passwordEncoder.matches(signoutRequestDto.getPassword(), user.getPassword())) {
            //유저 status정보를 NON_WITHDRAWAL -> WITHDRAWAL로
            user.update();
        } else throw new WrongPasswordException();
    }
}
