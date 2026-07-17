package com.caring.domain.report.service;

import com.caring.domain.connection.entity.Connection;
import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.report.entity.ReportSetting;
import com.caring.domain.report.repository.ReportSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportSettingService {

    private final ReportSettingRepository reportSettingRepository;
    private final ConnectionRepository connectionRepository;

    private static final LocalTime DEFAULT_REPORT_TIME = LocalTime.of(21, 0);

    /**
     * 읽기 전용 — 권한 검증 없이 "지금 적용되는 마감시간"만 조회
     * - 대상자 본인의 기분 체크 마감시간 판단, 배치 스케줄러 등 시스템 내부용
     */
    public LocalTime getEffectiveReportTime(Long wardId) {
        return reportSettingRepository.findByWardId(wardId)
                .map(ReportSetting::getEffectiveReportTime)
                .orElse(DEFAULT_REPORT_TIME);
    }

    /**
     * 쓰기 — 보호자가 자신의 대상자 레포트 시간을 변경
     * - 연결된 보호자인지 권한 검증 필수
     */
    @Transactional
    public void updateReportTime(Long protectorId, Long wardId, LocalTime newTime) {

        Connection connection = connectionRepository.findByProtector_MemberIdAndWard_MemberId(protectorId, wardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자에 대한 권한이 없습니다."));

        Optional<ReportSetting> existing = reportSettingRepository.findByWardId(wardId);

        if (existing.isPresent()) {
            existing.get().updateReportTime(newTime);
        } else {
            ReportSetting newSetting = ReportSetting.builder()
                    .ward(connection.getWard())
                    .reportTime(newTime)
                    .build();
            reportSettingRepository.save(newSetting);
        }
    }
}