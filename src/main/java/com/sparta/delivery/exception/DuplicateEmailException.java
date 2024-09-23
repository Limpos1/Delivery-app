package com.sparta.delivery.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("사용할 수 없는 이메일 입니다.");
    }
}
