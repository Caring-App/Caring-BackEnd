package com.caring.domain.health.repository;

import com.caring.domain.health.entity.HealthRecord;
import com.caring.domain.member.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    /**
     * 특정 ward의 특정 기간(하루) 내 건강 수치 기록 전체 조회
     * - 하루 여러 건 있을 수 있어 리스트로 반환
     * - DailyReportService에서 오늘자 혈당/혈압 등 최신값 추출할 때 사용
     */
    List<HealthRecord> findAllByWardMemberIdAndRecordedAtBetween(
            Long wardId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    /**
     * 특정 ward의 특정 기간 내, 특정 질병(disease)에 대한 건강 수치 기록 전체 조회
     * - 건강 수치 그래프(기간 누적 조회) API에서 사용
     * - 하루 여러 건이면 recordedAt 오름차순으로 반환되므로,
     *   Service에서 날짜별로 묶어 최신값만 추출하는 로직과 함께 사용
     */
    List<HealthRecord> findAllByWardMemberIdAndDiseaseAndRecordedAtBetweenOrderByRecordedAtAsc(
            Long wardId, Disease disease, LocalDateTime start, LocalDateTime end);
}