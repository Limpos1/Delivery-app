package com.sparta.delivery.auth.service;

import com.sparta.delivery.auth.dto.LoginRequestDto;
import com.sparta.delivery.config.JwtUtil;
import com.sparta.delivery.config.PasswordEncoder;
import com.sparta.delivery.exception.NoSignedUserException;
import com.sparta.delivery.exception.WrongPasswordException;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserStatus;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String login(LoginRequestDto requestDto) {

        //입력된 이메일로 유저찾기
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(() -> new NoSignedUserException());

        //비밀번호 일치하는지 확인
        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())){
            throw new WrongPasswordException();
        }

        //탈퇴유저 로그인 방지
        if(user.getStatus() != UserStatus.NON_WITHDRAWAL) {
            throw new NoSignedUserException();
        }

        //정상유저라면 JWT 토큰 반환
        return jwtUtil.createToken(
                user.getId(),
                user.getEmail()
        );
    }
}

