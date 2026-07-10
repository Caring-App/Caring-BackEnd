package com.caring.global.scheduler;

import com.caring.domain.pill.entity.PillSchedule;
import com.caring.domain.pill.repository.PillScheduleRepository;
import com.caring.domain.notification.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PillNotificationScheduler {

    private final PillScheduleRepository pillScheduleRepository;
    private final FcmService fcmService;

    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendPillNotifications() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        String todayKr = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

        log.info("[스케줄러 펑션 가동] 현재 시각: {}, 요일: {}", now, todayKr);

        List<PillSchedule> activeSchedules = pillScheduleRepository.findActiveSchedulesByTime(now);

        for (PillSchedule schedule : activeSchedules) {
            String takeDays = schedule.getTakeDays();

            if (takeDays.equals("EVERYDAY") || takeDays.contains(todayKr)) {
                String fcmToken = schedule.getWard().getFcmToken();
                String wardName = schedule.getWard().getName();
                String pillLabel = schedule.getPillName().getDescription();

                if (fcmToken != null && !fcmToken.isBlank()) {
                    log.info("[푸시 알림 발송 시도] 대상자: {}, 약물 타입: {}", wardName, pillLabel);

                    String title = "💊 복약 시간 알림";
                    String body = wardName + " 어르신, [" + pillLabel + "] 드실 시간입니다! 잊지 말고 챙겨 드세요.";

                    Map<String, String> dataPayload = new HashMap<>();
                    dataPayload.put("alarmType", schedule.getAlarmType().name());
                    dataPayload.put("voiceFileUrl", schedule.getVoiceFileUrl() != null ? schedule.getVoiceFileUrl() : "");

                    try {
                        fcmService.sendNotificationWithData(fcmToken, title, body, dataPayload);
                    } catch (Exception e) {
                        log.error("[FCM 발송 실패] 대상자 ID: {}, 에러: {}", schedule.getWard().getMemberId(), e.getMessage());
                    }
                } else {
                    log.warn("[FCM 발송 패스] 대상자 {}의 FCM 토큰이 존재하지 않습니다.", wardName);
                }
            }
        }
    }
}