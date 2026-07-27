package com.caring.global.scheduler;

import com.caring.domain.report.entity.ReportSetting;
import com.caring.domain.report.repository.ReportSettingRepository;
import com.caring.domain.report.repository.DailyReportRepository;
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
    private final DailyReportRepository dailyReportRepository;
    private final DailyReportService dailyReportService;

    @Scheduled(cron = "0 * * * * *")
    public void checkAndGenerateDailyReports() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        // 현재 시각이 마감시간인 ReportSetting들 조회
        List<ReportSetting> targetSettings = reportSettingRepository.findByReportTime(now);

        for (ReportSetting setting : targetSettings) {
            Long wardId = setting.getWard().getMemberId();

            // 오늘 이미 리포트가 생성됐는지 중복 체크
            boolean alreadyGenerated = dailyReportRepository.existsByWardMemberIdAndReportDate(wardId,LocalDate.now());

            if (alreadyGenerated) {
                log.info("[리포트 스케줄러] 이미 생성됨 - wardId: {}", wardId);
                continue;
            }

            // try-catch로 감싸서 한 명 실패해도 나머지는 계속 처리되게
            try {
                dailyReportService.generateReport(wardId);
                log.info("[리포트 생성 완료] wardId: {}", wardId);
            } catch (Exception e) {
                log.error("[리포트 생성 실패] wardId: {}, 에러: {}", wardId, e.getMessage());
            }
        }
    }
}