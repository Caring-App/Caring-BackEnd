package com.caring.domain.report.repository;

import com.caring.domain.report.entity.MoodCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MoodCheckRepository extends JpaRepository<MoodCheck, Long> {
    /**
     * 특정 ward의 특정 날짜 기분 기록 조회
     * - upsert 로직(있으면 UPDATE, 없으면 INSERT)의 있는지 확인 단계에서 사용
     * - 마감시간 체크 후 오늘자 기록을 가져올 때도 동일하게 사용
     */
    Optional<MoodCheck> findByWardMemberIdAndRecordDate(Long wardId, LocalDate recordDate);
}

