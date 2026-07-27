package com.caring.domain.report.service;

import com.caring.domain.connection.entity.Connection;
import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
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
     * 돌봄대상자 회원가입 시 기본 리포트 시간(21:00) 설정 생성
     * - MemberService에서 ward 생성 직후 호출
     */
    @Transactional
    public void createDefaultSetting(Member ward) {
        ReportSetting newReport = ReportSetting.builder()
                .ward(ward)
                .reportTime(DEFAULT_REPORT_TIME)
                .build();
        reportSettingRepository.save(newReport);
    }

    /**
     * 읽기 전용 — 권한 검증 없이 "지금 적용되는 마감시간"만 조회
     * - 대상자 본인의 기분 체크 마감시간 판단, 배치 스케줄러 등 시스템 내부용
     */
    public LocalTime getEffectiveReportTime(Long wardId) {
        return reportSettingRepository.findByWardMemberId(wardId)
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

        Optional<ReportSetting> existing = reportSettingRepository.findByWardMemberId(wardId);

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