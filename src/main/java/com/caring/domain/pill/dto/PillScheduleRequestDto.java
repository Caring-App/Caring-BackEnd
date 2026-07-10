package com.caring.domain.pill.dto;

import com.caring.domain.pill.entity.AlarmType;
import com.caring.domain.pill.entity.PillType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class PillScheduleRequestDto {
    private Long memberId;
    private PillType pillName;
    private String takeDays;
    private LocalTime takeTime;
    private Integer retryAlarm;
    private AlarmType alarmType;
    private String voiceFileUrl;

    @JsonProperty("isActive")
    private boolean isActive;
}
