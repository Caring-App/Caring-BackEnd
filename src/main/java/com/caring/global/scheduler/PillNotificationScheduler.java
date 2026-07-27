package com.caring.global.scheduler;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.notification.service.NotificationLogService;
import com.caring.domain.pill.entity.PillLog;
import com.caring.domain.pill.entity.PillSchedule;
import com.caring.domain.pill.repository.PillLogRepository;
import com.caring.domain.pill.repository.PillScheduleRepository;
import com.caring.domain.notification.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final PillLogRepository pillLogRepository;
    private final ConnectionRepository connectionRepository;
    private final NotificationLogService notificationLogService;

    // 최초 복약 알림
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendPillNotifications() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        String todayKr = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

        log.info("[스케줄러 펑션 가동] 현재 시각: {}, 요일: {}", now, todayKr);

        List<PillSchedule> activeSchedules = pillScheduleRepository.findActiveSchedulesByTime(now);

        for (PillSchedule schedule : activeSchedules) {
            String takeDays = schedule.getTakeDays();

            if (takeDays.equals("EVERYDAY") || takeDays.contains(todayKr)) {

                PillLog pillLog = pillLogRepository.findByPillScheduleAndRecordDate(schedule,LocalDate.now())
                        .orElseGet(()->pillLogRepository.save(
                                PillLog.builder()
                                .pillSchedule(schedule)
                                .recordDate(LocalDate.now())
                                .build()));

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
                    dataPayload.put("pillLogId", pillLog.getPillLogId().toString());

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

    // 미복약시 재알림 + 최종 보호자 알림
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendRetryNotifications() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        // 1. 미확인 로그들 조회 (findPendingLogsByDate, maxRetry는 PillLog.ESCALATED로)
        List<PillLog> pendingLogs = pillLogRepository.findPendingLogsByDate(LocalDate.now(), PillLog.ESCALATED);

        for (PillLog pillLog : pendingLogs) {
            PillSchedule schedule = pillLog.getPillSchedule();
            int retryCount = pillLog.getCurrentRetryCount();

            // 2. 다음 재알림 시각 계산
            LocalTime nextActionTime = schedule.getTakeTime()
                    .plusMinutes((long) (retryCount+1)*schedule.getRetryAlarm());

            // 3. 지금이 그 시각이 아니면 이번 루프는 건너뛰기 (continue)
            if (!now.equals(nextActionTime)){
                continue;
            }

            // 4. 재알림 횟수가 3 이상이면 -> 보호자 알림 + escalate()
            if (retryCount>=3) {
                // 3번 재알림 완료 된 상태 -> 보호자에게 알림, 재시도 X
                notifyProtector(schedule, pillLog);
                pillLog.escalate();
            } else{
                String fcmToken = schedule.getWard().getFcmToken();
                String wardName = schedule.getWard().getName();
                String pillLabel = schedule.getPillName().getDescription();

                if (fcmToken != null && !fcmToken.isBlank()) {
                    log.info("[푸시 알림 발송 시도] 대상자: {}, 약물 타입: {}", wardName, pillLabel);

                    String title = "💊 복약  재알림";
                    String body = wardName + " 어르신, 아직 [" + pillLabel + "] 확인이 안됐어요. 다시 확인해주세요!";

                    Map<String, String> dataPayload = new HashMap<>();
                    dataPayload.put("alarmType", schedule.getAlarmType().name());
                    dataPayload.put("voiceFileUrl", schedule.getVoiceFileUrl() != null ? schedule.getVoiceFileUrl() : "");
                    dataPayload.put("pillLogId", pillLog.getPillLogId().toString());

                    try {
                        fcmService.sendNotificationWithData(fcmToken, title, body, dataPayload);
                    } catch (Exception e) {
                        log.error("[재알림 발송 실패] 에러: {}", e.getMessage());
                    } finally {
                        pillLog.increaseRetryCount();
                    }
                }
            }
        }
    }

    // 보호자에게 최종 알림
    private void notifyProtector(PillSchedule schedule, PillLog pillLog) {
        Member ward = schedule.getWard();

        connectionRepository.findByWard(ward).ifPresentOrElse(connection -> {
            Member protector = connection.getProtector();

            String title = "⚠️ 미응답 알림";
            String body = ward.getName() + " 님이 [" + schedule.getPillName().getDescription() + "] 복약 확인을 하지 않으셨어요.";

            notificationLogService.saveLog(protector, title, body);

            String protectorFcmToken = protector.getFcmToken();
            if (protectorFcmToken != null && !protectorFcmToken.isBlank()) {
                try {
                    fcmService.sendNotification(protectorFcmToken, title, body);
                } catch (Exception e) {
                    log.error("[보호자 알림 발송 실패] 에러: {}", e.getMessage());
                }
            }
        }, () -> log.warn("[보호자 알림 패스] 대상자 {}와 연결된 보호자가 없습니다.", ward.getName()));
    }
}