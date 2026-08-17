package com.caring.domain.health.repository;

import com.caring.domain.health.entity.StepRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StepRecordRepository extends JpaRepository<StepRecord, Long> {

    /**
     * 특정 ward의 특정 날짜 걸음 수 기록 단건 조회
     * - upsert 시 기존 row 존재 여부 확인용
     * - DailyReportService에서 오늘자 걸음 수 조회 시에도 사용
     */
    Optional<StepRecord> findByWardMemberIdAndRecordedDate(Long wardId, LocalDate recordedDate);

    /**
     * 특정 ward의 특정 기간 내 걸음 수 기록 전체 조회
     * - 건강 수치 그래프(기간 누적 조회) API에서 사용
     * - step_record는 하루 1건(upsert)이라 날짜별 최신값 추출 로직 없이
     *   조회 결과 그대로 날짜별 값으로 사용 가능
     */
    List<StepRecord> findAllByWardMemberIdAndRecordedDateBetweenOrderByRecordedDateAsc(
            Long wardId, LocalDate start, LocalDate end);
}