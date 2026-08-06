package com.caring.domain.report.repository;

import com.caring.domain.report.entity.DailyReportHealthDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyReportHealthDetailRepository extends JpaRepository<DailyReportHealthDetail, Long> {

    /**
     * 특정 daily_report의 질병별 상세 수치 전체 조회
     */
    List<DailyReportHealthDetail> findByDailyReportId (Long dailyReportId);
}
