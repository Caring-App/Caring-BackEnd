package com.caring.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDto {
    // 에러 응답 DTO
    private int status;
    private String message;
}
