package com.caring.domain.schedule.dto;

import com.caring.global.common.AlarmType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class TaskScheduleRequestDto {
    private String taskName;
    private String locationName;
    private LocalDate taskDate;
    private LocalTime taskTime;
    private LocalTime ttsVoiceTime;
    private String ttsMessage;
    private AlarmType alarmType;
    private String voiceFileUrl;
}
