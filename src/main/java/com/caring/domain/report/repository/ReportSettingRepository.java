package com.caring.domain.report.repository;

import com.caring.domain.report.entity.ReportSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportSettingRepository extends JpaRepository<ReportSetting, Long> {
    /**
     * 특정 ward의 레포트 시간 설정 조회
     * - ward당 설정은 1건뿐 (1:1 관계, DB 유니크 제약)
     * - 없으면 아직 설정을 안 한 상태 → 서비스 레이어에서 기본값(21:00) 처리
     */
    Optional<ReportSetting> findByWardMemberId(Long wardId);
}
