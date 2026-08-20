package com.caring.domain.schedule.entity;

import com.caring.domain.member.entity.Member;
import com.caring.global.common.AlarmType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "task_schedule")
public class TaskSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Member ward;

    @Column(name = "task_name", length = 100, nullable = false)
    private String taskName;

    @Column(name = "location_name", length = 100)
    private String locationName;

    @Column(name = "task_date", nullable = false)
    private LocalDate taskDate;

    @Column(name = "task_time", nullable = false)
    private LocalTime taskTime;

    @Column(name = "tts_voice_time")
    private LocalTime ttsVoiceTime;

    @Column(name = "tts_message", columnDefinition = "TEXT")
    private String ttsMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type", nullable = false, length = 50)
    private AlarmType alarmType;

    @Column(name = "voice_file_url", length = 500)
    private String voiceFileUrl;

    @Column(name = "place_id")
    private Long placeId;

    public void updateTask(String taskName,
                           String locationName,
                           LocalDate taskDate,
                           LocalTime taskTime,
                           LocalTime ttsVoiceTime,
                           String ttsMessage,
                           AlarmType alarmType,
                           String voiceFileUrl,
                           Long placeId) {
        this.taskName = taskName;
        this.locationName = locationName;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.ttsVoiceTime = ttsVoiceTime;
        this.ttsMessage = ttsMessage;
        this.alarmType = alarmType;
        this.voiceFileUrl = voiceFileUrl;
        this.placeId = placeId;
    }

    public void updateVoiceFile(String voiceFileUrl) {
        this.voiceFileUrl = voiceFileUrl;
    }
}
