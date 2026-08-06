package com.caring.domain.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DailyReportResponseDto {

    private Long reportId;
    private LocalDate reportDate;
    private String moodStatus;
    private Integer steps;
    private String healthSummary;
    private Double medicationRate;
    private Boolean isDelivered;
    private List<HealthDetailDto> healthDetails;

    @Getter
    @Builder
    public static class HealthDetailDto {
        private String diseaseName;
        private Double avgValue;
    }
}