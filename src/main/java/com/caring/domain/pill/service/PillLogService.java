package com.caring.domain.pill.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.notification.service.FcmService;
import com.caring.domain.notification.service.NotificationLogService;
import com.caring.domain.pill.dto.PillLogResponseDto;
import com.caring.domain.pill.entity.PillLog;
import com.caring.domain.pill.entity.PillSchedule;
import com.caring.domain.pill.repository.PillLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PillLogService {

    private final PillLogRepository pillLogRepository;
    private final ConnectionRepository connectionRepository;
    private final FcmService fcmService;
    private final NotificationLogService notificationLogService;

    @Transactional
    public PillLogResponseDto confirmPill(Long wardId, Long pillLogId){

        //1. Pilllogid로 Pilllog 조회
        PillLog pillLog = pillLogRepository.findById(pillLogId)
                .orElseThrow(() -> new IllegalArgumentException("복약 기록이 없습니다."));

        Long ownerWardId = pillLog.getPillSchedule().getWard().getMemberId();
        if(!ownerWardId.equals(wardId)){
            throw new IllegalArgumentException("본인의 복약 기록만 확인할 수 있습니다.");
        }

        //2. 이미 확인된거면 예외
        if (pillLog.isTaken()) {
            throw new IllegalArgumentException("이미 확인된 복약입니다.");
        }
        // 3. confirm 호출 -> 확인 처리
        pillLog.confirm();

        notifyProtectorOfCompletion(pillLog);

        // 4. DTO로 변환해서 반환
        return new PillLogResponseDto(pillLog);

    }



    private void notifyProtectorOfCompletion(PillLog pillLog) {
        PillSchedule schedule = pillLog.getPillSchedule();
        Member ward = schedule.getWard();
        String pillLabel = schedule.getPillName().getDescription();

        connectionRepository.findByWard(ward).ifPresentOrElse(connection -> {
            Member protector = connection.getProtector();

            String title = "✅ 복약 완료";
            String body = ward.getName() + " 님 [" + pillLabel + "] 복용이 확인되었습니다.";

            notificationLogService.saveLog(protector, title, body);

            String protectorFcmToken = protector.getFcmToken();
            if (protectorFcmToken != null && !protectorFcmToken.isBlank()) {
                try {
                    fcmService.sendNotification(protectorFcmToken, title, body);
                } catch (Exception e) {
                    log.error("[복약 완료 알림 발송 실패] 에러: {}", e.getMessage());
                }
            }
        }, () -> log.warn("[복약 완료 알림 패스] 대상자 {}와 연결된 보호자가 없습니다.", ward.getName()));
    }
}
