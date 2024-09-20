package com.sparta.delivery.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUser {

    private final Long id;
    private final String email;

}
