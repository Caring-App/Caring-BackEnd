package com.caring.domain.report.dto;

import com.caring.domain.report.entity.MoodCheck;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MoodCheckRequestDto {
    private MoodCheck.MoodStatus moodStatus;
}
