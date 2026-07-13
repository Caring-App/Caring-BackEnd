package com.caring.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
// 모든 컨트롤러에서 발생하는 예외를 이 클래스가 가로챔
public class GlobalExceptionHandler {

    // 우리가 직접 던지는 예외들 (검증 실패, 중복 가입 등) 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e){
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(400)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 소셜 로그인 토큰이 유효하지 않을 떄 -> 401
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponseDto>handleHttpClientErrorException(HttpClientErrorException e) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(401)
                .message("소셜 로그인 인증에 실패했습니다")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

    }
}

