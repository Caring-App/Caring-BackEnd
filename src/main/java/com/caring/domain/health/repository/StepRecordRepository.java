package com.caring.domain.health.repository;

import com.caring.domain.health.entity.StepRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StepRecordRepository extends JpaRepository<StepRecord, Long> {

    /**
     * 특정 ward의 특정 날짜 걸음 수 기록 단건 조회
     * - upsert 시 기존 row 존재 여부 확인용
     * - DailyReportService에서 오늘자 걸음 수 조회 시에도 사용
     */
    Optional<StepRecord> findByWardIdAndRecordedDate(Long wardId, LocalDate recordedDate);
}