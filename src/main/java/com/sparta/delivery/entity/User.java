package com.sparta.delivery.entity;

import com.sparta.delivery.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Email
    private String email; // 사용자 아이디 (이메일)

    @Size(min = 8, message = "비밀번호는 최소 8글자 이상이어야 합니다.")
    private String password; // 비밀번호

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role; //USER, OWNER

}
