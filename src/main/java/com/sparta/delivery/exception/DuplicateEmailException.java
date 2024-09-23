package com.sparta.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("사용할 수 없는 이메일 입니다.");
    }
}
