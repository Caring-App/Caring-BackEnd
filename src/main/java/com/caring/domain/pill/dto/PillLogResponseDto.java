package com.caring.domain.pill.dto;

import com.caring.domain.pill.entity.PillLog;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class PillLogResponseDto {

    private Long pillLogId;
    private Long pillScheduleId;   // PillLog가 갖고 있는 PillSchedule 객체에서 id만 꺼내옴
    private LocalDate recordDate;
    private boolean isTaken;
    private LocalDateTime confirmedAt;
    private Integer currentRetryCount;

    // pliiLog 엔티티를 받아서 DTO로 변환해주는 생성자
    public PillLogResponseDto(PillLog entity){
        this.pillLogId = entity.getPillLogId();
        this.pillScheduleId = entity.getPillSchedule().getPillScheduleId();
        this.recordDate = entity.getRecordDate();
        this.isTaken = entity.isTaken();
        this.confirmedAt=entity.getConfirmedAt();
        this.currentRetryCount=entity.getCurrentRetryCount();
    }
}
