package com.sparta.delivery.exception;

import com.sparta.delivery.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ApiResponse<?> handleDuplicateEmailException(DuplicateEmailException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ApiResponse<?> handleWrongPasswordException(WrongPasswordException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(NoSignedUserException.class)
    public ApiResponse<?> handleNoSignedUserException(NoSignedUserException e) {
        return ApiResponse.createError(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error ->
                errors.add(error.getDefaultMessage())
        );
        return getErrorResponse(status, String.join(",", errors));
    }

    //오류 메세지 내용
    public ResponseEntity<Map<String, Object>> getErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.name());
        errorResponse.put("code", status.value());
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleAllException(Exception e) {
//        return new ResponseEntity<>("서버 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
