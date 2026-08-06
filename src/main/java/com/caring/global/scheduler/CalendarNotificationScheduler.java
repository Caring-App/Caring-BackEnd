package com.caring.global.scheduler;

import com.caring.domain.notification.service.FcmService;
import com.caring.domain.schedule.entity.TaskSchedule;
import com.caring.domain.schedule.repository.TaskScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalendarNotificationScheduler {
    private final TaskScheduleRepository taskScheduleRepository;
    private final FcmService fcmService;

    // 캘린더 TTS 발송
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendCalendarNotifications() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        LocalDate today = LocalDate.now();

        log.info("[캘린더 스케줄러 가동] 현재 시각: {}", now);

        List<TaskSchedule> targetSchedule = taskScheduleRepository.findByTaskDateAndTtsVoiceTime(today, now);

        for(TaskSchedule schedule : targetSchedule) {
            String fcmToken = schedule.getWard().getFcmToken();
            String wardName = schedule.getWard().getName();
            String taskName = schedule.getTaskName();

            if(fcmToken != null && !fcmToken.isBlank()) {
                log.info("[캘린더 알림 발송 시도] 대상자: {}, 일정: {}", wardName, taskName);

                String title = "🔔 오늘의 일정 안내";
                String body = wardName + " 어르신, [" + taskName + "] 일정이 곧 있어요!";

                Map<String, String> dataPayload = new HashMap<>();
                dataPayload.put("taskId", schedule.getTaskId().toString());
                dataPayload.put("alarmType", schedule.getAlarmType().name());
                dataPayload.put("voiceFileUrl", schedule.getVoiceFileUrl() != null ? schedule.getVoiceFileUrl() : "");
                dataPayload.put("ttsMessage", schedule.getTtsMessage() != null ? schedule.getTtsMessage() : "");

                try {
                    fcmService.sendNotificationWithData(fcmToken, title, body, dataPayload);
                } catch(Exception e) {
                    log.error("[캘린더 알림 발송 실패] 대상자 ID: {}, 에러: {}", schedule.getWard().getMemberId(), e.getMessage());
                }
            } else {
                log.warn("[캘린더 알림 발송 패스] 대상자 {}의 FCM 토큰이 존재하지 않습니다.", wardName);
            }
        }
    }


}
