package com.sparta.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSignedUserException extends RuntimeException{
    public NoSignedUserException() { super("가입되지 않은 유저입니다.");}
}
