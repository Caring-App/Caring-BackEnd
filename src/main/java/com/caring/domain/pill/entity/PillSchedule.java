package com.caring.domain.pill.entity;

import com.caring.domain.member.entity.Member;
import com.caring.global.common.AlarmType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "pill_schedule")
public class PillSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pill_schedule_id")
    private Long pillScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Member ward;

    @Enumerated(EnumType.STRING)
    @Column(name = "pill_name", length = 100, nullable = false)
    private PillType pillName;

    @Column(name = "take_days", length = 50, nullable = false)
    private String takeDays;

    @Column(name = "take_time", nullable = false)
    private LocalTime takeTime;

    @Builder.Default
    @Column(name = "retry_alarm", nullable = false)
    private Integer retryAlarm = 10;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type", nullable = false, length = 50)
    private AlarmType alarmType = AlarmType.TTS;

    @Column(name = "voice_file_url", length = 500)
    private String voiceFileUrl;

    @Column(name = "retry_voice_file_url", length = 500)
    private String retryVoiceFileUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public void updateSchedule(PillType pillName,
                               String takeDays,
                               LocalTime takeTime,
                               Integer retryAlarm,
                               AlarmType alarmType,
                               String voiceFileUrl,
                               String retryVoiceFileUrl) {
        this.pillName = pillName;
        this.takeDays = takeDays;
        this.takeTime = takeTime;
        this.retryAlarm = retryAlarm;
        this.alarmType = alarmType;
        this.voiceFileUrl = voiceFileUrl;
        this.retryVoiceFileUrl = retryVoiceFileUrl;
    }

    public void toggleActiveStatus(boolean isActive) {
        this.isActive = isActive;
    }
}
