package com.caring.domain.schedule.dto;

import com.caring.domain.schedule.entity.TaskSchedule;
import com.caring.global.common.AlarmType;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class TaskScheduleResponseDto {
    private final Long taskId;
    private final String taskName;
    private final String locationName;
    private final LocalDate taskDate;
    private final LocalTime taskTime;
    private final LocalTime ttsVoiceTime;
    private final String ttsMessage;
    private final AlarmType alarmType;
    private final String voiceFileUrl;
    private Long placeId;

    public TaskScheduleResponseDto(TaskSchedule entity) {
        this.taskId = entity.getTaskId();
        this.taskName = entity.getTaskName();
        this.locationName = entity.getLocationName();
        this.taskDate = entity.getTaskDate();
        this.taskTime = entity.getTaskTime();
        this.ttsVoiceTime = entity.getTtsVoiceTime();
        this.ttsMessage = entity.getTtsMessage();
        this.alarmType = entity.getAlarmType();
        this.voiceFileUrl = entity.getVoiceFileUrl();
        this.placeId = entity.getPlaceId();
    }
}
