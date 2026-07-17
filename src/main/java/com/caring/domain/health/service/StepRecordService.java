package com.caring.domain.health.service;

import com.caring.domain.health.entity.StepRecord;
import com.caring.domain.health.repository.StepRecordRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.report.service.ReportSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class StepRecordService {

    private final StepRecordRepository stepRecordRepository;
    private final MemberRepository memberRepository;
    private final ReportSettingService reportSettingService;

    /**
     * 대상자 본인이 오늘의 걸음 수 입력
     * - HealthRecord와 동일하게 하루 여러 번 기록 가능한 단순 INSERT
     * - 레포트 마감시간 이후에는 입력 불가
     */
    @Transactional
    public void recordSteps(Long wardId, Integer steps) {

        LocalTime deadline = reportSettingService.getEffectiveReportTime(wardId);

        if (!LocalTime.now().isBefore(deadline)) {
            throw new IllegalStateException("지금은 걸음 수를 입력할 수 없습니다.");
        }

        Member ward = memberRepository.findById(wardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 돌봄대상자입니다."));

        StepRecord stepRecord = StepRecord.builder()
                .ward(ward)
                .steps(steps)
                .build();
        stepRecordRepository.save(stepRecord);
    }
}
