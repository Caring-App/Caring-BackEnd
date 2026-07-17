package com.caring.domain.health.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.health.dto.StepRecordResponseDto;
import com.caring.domain.health.entity.StepRecord;
import com.caring.domain.health.repository.StepRecordRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.report.service.ReportSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StepRecordService {

    private final StepRecordRepository stepRecordRepository;
    private final MemberRepository memberRepository;
    private final ReportSettingService reportSettingService;
    private final ConnectionRepository connectionRepository;

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

    /**
     * 보호자가 대상자의 오늘 걸음수 기록 조회
     */
    public List<StepRecordResponseDto> getTodaySteps(Long protectorId, Long wardId) {

        // 1. 권한 검증 (연결 안 돼있으면 예외)
        connectionRepository.findByProtector_MemberIdAndWard_MemberId(protectorId, wardId)
                .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다."));

        // 2. 오늘자 기록 전체 조회
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        List<StepRecord> records = stepRecordRepository.findAllByWardIdAndRecordedAtBetween(wardId, startOfDay, endOfDay);

        // 3. DTO 리스트로 변환
        return records.stream()
                .map(r -> StepRecordResponseDto.builder()
                        .steps(r.getSteps())
                        .recordedAt(r.getRecordedAt())
                        .build())
                .toList();
    }
}
