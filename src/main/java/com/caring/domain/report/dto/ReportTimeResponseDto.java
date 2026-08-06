package com.caring.domain.report.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalTime;

@Getter
@Builder
public class ReportTimeResponseDto {
    private LocalTime reportTime;
}
