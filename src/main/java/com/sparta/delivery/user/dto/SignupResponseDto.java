package com.sparta.delivery.user.dto;

import com.sparta.delivery.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SignupResponseDto implements Serializable {

    private final Long id;
    private final String email;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public SignupResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.createdAt = user.getCreatedAt();
        this.modifiedAt = user.getModifiedAt();
    }
}
