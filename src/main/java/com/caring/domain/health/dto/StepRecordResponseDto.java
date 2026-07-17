package com.caring.domain.health.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StepRecordResponseDto {
    private Integer steps;
    private LocalDateTime recordedAt;
}