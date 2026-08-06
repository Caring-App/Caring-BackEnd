package com.caring.domain.health.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HealthRecordResponseDto {
    private String diseaseName;
    private Integer healthValue;
    private LocalDateTime recordedAt;
}