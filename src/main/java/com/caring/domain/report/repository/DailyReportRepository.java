package com.caring.domain.report.repository;

import com.caring.domain.report.entity.DailyReport;
import com.caring.domain.report.entity.ReportSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    /**
     * 특정 ward의 특정 날짜 레포트 조회
     * - 배치가 중복 생성하지 않도록 "오늘자 레포트가 이미 있는지" 확인할 때 사용
     * - 오늘의 요약 화면 조회에도 동일하게 사용
     */
    Optional<DailyReport> findByWardMemberIdAndReportDate(Long wardId, LocalDate reportDate);

    /**
     * 특정 ward의 기간별 레포트 목록 조회 (그래프용)
     * - 최근 N일치를 날짜 오름차순으로 반환 → 프론트에서 그대로 시계열 그래프에 사용 가능
     */
    List<DailyReport> findByWardMemberIdAndReportDateBetweenOrderByReportDateAsc(
            Long wardId, LocalDate startDate, LocalDate endDate);

    List<ReportSetting> findByReportTime(LocalTime reportTime);
}
