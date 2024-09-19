package com.sparta.delivery.entity;

import com.sparta.delivery.enums.UserRole;
import com.sparta.delivery.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;


@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

}
