package com.caring.domain.pill.dto;

import com.caring.domain.pill.entity.AlarmType;
import com.caring.domain.pill.entity.PillSchedule;
import com.caring.domain.pill.entity.PillType;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class PillScheduleResponseDto {
    private final Long pillScheduleId;
    private final Long memberId;
    private final String wardName;
    private final PillType pillName;
    private final String pillNameKr;
    private final String takeDays;
    private final LocalTime takeTime;
    private final Integer retryAlarm;
    private final AlarmType alarmType;
    private final String voiceFileUrl;
    private final boolean isActive;

    public PillScheduleResponseDto(PillSchedule entity) {
        this.pillScheduleId = entity.getPillScheduleId();
        this.memberId = entity.getMember().getMemberId();
        this.wardName = entity.getMember().getName();
        this.pillName = entity.getPillName();
        this.pillNameKr = entity.getPillName().getDescription();
        this.takeDays = entity.getTakeDays();
        this.takeTime = entity.getTakeTime();
        this.retryAlarm = entity.getRetryAlarm();
        this.alarmType = entity.getAlarmType();
        this.voiceFileUrl = entity.getVoiceFileUrl();
        this.isActive = entity.isActive();
    }
}
