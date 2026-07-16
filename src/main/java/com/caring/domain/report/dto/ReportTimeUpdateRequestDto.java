package com.caring.domain.report.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ReportTimeUpdateRequestDto {
    private LocalTime reportTime;
}
