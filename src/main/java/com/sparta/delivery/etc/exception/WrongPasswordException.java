package com.sparta.delivery.etc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException() {super("잘못된 비밀번호입니다.");}
}
