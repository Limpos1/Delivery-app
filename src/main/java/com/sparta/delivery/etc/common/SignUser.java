package com.sparta.delivery.etc.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUser {

    private final Long id;
    private final String email;
}
