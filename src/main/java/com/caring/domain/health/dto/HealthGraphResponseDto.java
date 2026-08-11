package com.caring.domain.health.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class HealthGraphResponseDto {

    private List<DailyValueDto> bloodSugar;      // 당뇨병 미등록 시 null
    private List<DailyValueDto> bloodPressure;   // 고혈압 미등록 시 null
    private List<DailyValueDto> steps;            // 무조건 값 있음

    @Getter
    @Builder
    public static class DailyValueDto {
        private LocalDate date;
        private Integer value;
    }
}