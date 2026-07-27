package com.caring.global.scheduler;

import com.caring.domain.report.entity.ReportSetting;
import com.caring.domain.report.repository.ReportSettingRepository;
import com.caring.domain.report.repository.DailyReportRepository; // TODO: 실제 패키지명 확인
import com.caring.domain.report.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyReportScheduler {

    private final ReportSettingRepository reportSettingRepository;
    private final DailyReportRepository dailyReportRepository; // 중복 체크용
    private final DailyReportService dailyReportService;

    @Scheduled(cron = "0 * * * * *")
    public void checkAndGenerateDailyReports() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        // TODO 1: 현재 시각(now)이 마감시간인 ReportSetting들 조회
        //   - PillSchedule의 findActiveSchedulesByTime(now) 참고해서
        //     ReportSettingRepository에 findByReportTime(LocalTime) 같은 메서드 추가 필요
        //   - 단, ReportSetting.reportTime이 null인 경우(기본값 21:00 미설정) 처리 방법은
        //     세연님이 위 질문 답변 주시면 같이 정하기
        List<ReportSetting> targetSettings = null; // TODO

        for (ReportSetting setting : targetSettings) {
            Long wardId = setting.getWard().getMemberId(); // Member PK가 memberId인 거 잊지 마세요!

            // TODO 2: 오늘 이미 리포트가 생성됐는지 중복 체크
            //   - PillNotificationScheduler의 findByPillScheduleAndRecordDate 패턴처럼
            //     DailyReportRepository에 existsByWard_MemberIdAndReportDate(wardId, LocalDate) 같은
            //     메서드가 필요할 거예요
            boolean alreadyGenerated = false; // TODO

            if (alreadyGenerated) {
                log.info("[리포트 스케줄러] 이미 생성됨 - wardId: {}", wardId);
                continue;
            }

            // TODO 3: 개별 try-catch로 감싸서 한 명 실패해도 나머지는 계속 처리되게
            try {
                // TODO: dailyReportService.generateReport(wardId) 호출
                log.info("[리포트 생성 시도] wardId: {}", wardId);
            } catch (Exception e) {
                log.error("[리포트 생성 실패] wardId: {}, 에러: {}", wardId, e.getMessage());
            }
        }
    }
}