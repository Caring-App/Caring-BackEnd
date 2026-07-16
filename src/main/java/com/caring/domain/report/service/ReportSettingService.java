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

    public LocalTime getEffectiveReportTime(Long protectorId, Long wardId) {

        connectionRepository.findByProtector_MemberIdAndWard_MemberId(protectorId, wardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자에 대한 권한이 없습니다."));

        return reportSettingRepository.findByWardId(wardId)
                .map(ReportSetting::getEffectiveReportTime)
                .orElse(ReportSetting.DEFAULT_REPORT_TIME);
    }

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