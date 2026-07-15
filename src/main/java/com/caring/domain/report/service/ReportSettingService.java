package com.caring.domain.report.service;

import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    private static final LocalTime DEFAULT_REPORT_TIME = LocalTime.of(21, 0);

    /**
     * 보호자가 설정한 (또는 기본값) 레포트 시간 조회
     * - MoodCheckService의 마감시간 체크, DailyReportScheduler의 발행 시점 판단에 공통으로 쓰임
     */
    public LocalTime getEffectiveReportTime(Long wardId) {

     return reportSettingRepository.findByWardId(wardId)
                .map(ReportSetting::getEffectiveReportTime)
                .orElse(DEFAULT_REPORT_TIME);
    }

    /**
     * 보호자가 레포트 시간을 변경 (최초 설정 or 기존 값 갱신)
     */
    @Transactional
    public void updateReportTime(Long wardId, LocalTime newTime) {
        // 1. reportSettingRepository.findByWardId(wardId) 조회
        Optional<ReportSetting> existing = reportSettingRepository.findByWardId(wardId);

        if (existing.isPresent()) {
            // 2. 존재하면 setting.getEffectiveReportTime() 반환
            existing.get().updateReportTime(newTime);
        } else {
            Member ward = memberRepository.findById(wardId)
                    .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 돌봄대상자입니다."));
            // 3. 존재하지 않으면 DEFAULT_REPORT_TIME 반환
            ReportSetting newSetting = ReportSetting.builder()
                    .ward(ward)
                    .reportTime(newTime)
                    .build();
            reportSettingRepository.save(newSetting);
        }
    }
}