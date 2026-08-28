package com.caring.domain.pill.dto;

import com.caring.domain.pill.entity.PillLog;
import com.caring.domain.pill.entity.PillSchedule;
import com.caring.domain.pill.entity.PillType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class PillTodayResponseDto {

    private Long pillScheduleId;
    private Long pillLogId;
    private PillType pillName;
    private String pillNameKr;
    private LocalTime takeTime;
    private LocalDate recordDate;
    private boolean isTaken;
    private LocalDateTime confirmedAt;

    public PillTodayResponseDto(PillSchedule schedule, PillLog pillLog){
        this.pillScheduleId = schedule.getPillScheduleId();
        this.pillLogId = pillLog.getPillLogId();
        this.pillName = schedule.getPillName();
        this.pillNameKr = schedule.getPillName().getDescription();
        this.takeTime = schedule.getTakeTime();
        this.recordDate = pillLog.getRecordDate();
        this.isTaken = pillLog.isTaken();
        this.confirmedAt = pillLog.getConfirmedAt();
    }
}
