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
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StepRecordService {

    private final StepRecordRepository stepRecordRepository;
    private final MemberRepository memberRepository;
    private final ReportSettingService reportSettingService;
    private final ConnectionRepository connectionRepository;

    /**
     * 대상자 걸음 수 기록 (안드로이드에서 자동 전송)
     * - 하루 1건 upsert 방식 (MoodCheck과 동일 패턴)
     * - steps는 "오늘 누적 총 걸음수" 값을 그대로 덮어씀
     */
    @Transactional
    public void recordSteps(Long wardId, Integer steps) {

        LocalTime deadline = reportSettingService.getEffectiveReportTime(wardId);

        if (!LocalTime.now().isBefore(deadline)) {
            throw new IllegalStateException("지금은 걸음 수를 입력할 수 없습니다.");
        }

        LocalDate today = LocalDate.now();

        // 기존 걸음수 조회
        Optional<StepRecord> exsitngRecord = stepRecordRepository.findByWardIdAndRecordedDate(wardId,today);

        if (exsitngRecord.isPresent()){

            // 기존 걸음수가 있다면 값만 새로 값만 갱신
            StepRecord record = exsitngRecord.get(); // Optional에서 실제 객채 꺼내기
            record.updateSteps(steps);

        } else {

            // 없으면 Member 조회 후 save
            Member member = memberRepository.findById(wardId)
                    .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 돌봄대상자입니다."));
            StepRecord newRecord = StepRecord.builder()
                            .ward(member)
                            .steps(steps)
                            .build();

            stepRecordRepository.save(newRecord);
        }
    }

    /**
     * 보호자가 대상자의 오늘 걸음수 기록 조회
     */
    public StepRecordResponseDto getTodaySteps(Long protectorId, Long wardId) {

        // 1. 권한 검증 (연결 안 돼있으면 예외)
        connectionRepository.findByProtector_MemberIdAndWard_MemberId(protectorId, wardId)
                .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다."));

        // 2. 오늘 날짜로 조회
        LocalDate today = LocalDate.now();
        Optional<StepRecord> records = stepRecordRepository.findByWardIdAndRecordedDate(wardId,today);

        // 3. 있으면 DTO 리스트로 변환, 없으면 null 리턴 ( 아직 오늘의 기록이 없는 경우 )
        return records
                .map(r -> StepRecordResponseDto.builder()
                        .steps(r.getSteps())
                        .recordedDate(r.getRecordedDate())
                        .build())
                .orElse(null);
    }
}
