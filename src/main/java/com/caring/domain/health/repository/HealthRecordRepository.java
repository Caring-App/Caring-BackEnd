package com.caring.domain.health.repository;

import com.caring.domain.health.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    /**
     * 특정 ward의 특정 기간(하루) 내 건강 수치 기록 전체 조회
     * - 하루 여러 건 있을 수 있어 리스트로 반환
     * - DailyReportService에서 오늘자 혈당/혈압 등 최신값 추출할 때 사용
     */
    List<HealthRecord> findAllByWardIdAndRecordedAtBetween(
            Long wardId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}