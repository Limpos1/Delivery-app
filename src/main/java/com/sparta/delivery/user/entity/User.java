package com.sparta.delivery.user.entity;

import com.sparta.delivery.common.Timestamped;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable = false, length = 100)
    @Email
    private String email; // 사용자 아이디 (이메일)

    @Column(nullable = false, length = 100)
    private String password; // 비밀번호

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role; //USER, OWNER

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;



    public User(SignupRequestDto requestDto, String encodedPassword) {
        this.email = requestDto.getEmail();
        this.password = encodedPassword;
        this.role = requestDto.getRole();
        this.status = UserStatus.NON_WITHDRAWAL;
    }

    public void update() {
        this.status = UserStatus.WITHDRAWAL;
    }
}
