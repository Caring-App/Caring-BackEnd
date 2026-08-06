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

    // 우리가 직접 던지는 예외들 중 "상태(타이밍) 문제"로 인한 것들 (ex. 마감시간 지나서 입력 불가) 400
    // IllegalArgumentException과는 자바에서 별개의 타입이라 별도로 등록해줘야 이 핸들러가 잡아줌
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStateException(IllegalStateException e){
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