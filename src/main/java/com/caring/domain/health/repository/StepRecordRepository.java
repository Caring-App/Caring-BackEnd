package com.caring.domain.health.repository;

import com.caring.domain.health.entity.StepRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StepRecordRepository extends JpaRepository<StepRecord, Long> {

    /**
     * 특정 ward의 특정 기간(하루) 내 걸음 수 기록 전체 조회
     * - DailyReportService에서 오늘자 최신 걸음 수 추출할 때 사용
     */
    List<StepRecord> findAllByWardIdAndRecordedAtBetween(
            Long wardId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}