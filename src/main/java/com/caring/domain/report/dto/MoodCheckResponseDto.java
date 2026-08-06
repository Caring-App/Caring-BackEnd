package com.caring.domain.report.dto;

import com.caring.domain.report.entity.MoodCheck.MoodStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MoodCheckResponseDto {
    private MoodStatus moodStatus;
    private LocalDate recordDate;
    private LocalDateTime checkedAt;
}
