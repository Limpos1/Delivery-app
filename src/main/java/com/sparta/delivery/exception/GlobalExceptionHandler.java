package com.sparta.delivery.exception;

import com.sparta.delivery.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateEmailException(DuplicateEmailException e) {
        ApiResponse<?> response = ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ApiResponse<?>> handleWrongPasswordException(WrongPasswordException e) {
        ApiResponse<?> response =  ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSignedUserException.class)
    public ResponseEntity<ApiResponse<?>> handleNoSignedUserException(NoSignedUserException e) {
        ApiResponse<?> response = ApiResponse.createError(e.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllException(Exception e) {
        return new ResponseEntity<>("서버 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
