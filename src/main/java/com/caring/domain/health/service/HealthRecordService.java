package com.caring.domain.health.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.health.dto.HealthRecordResponseDto;
import com.caring.domain.health.entity.HealthRecord;
import com.caring.domain.health.repository.HealthRecordRepository;
import com.caring.domain.member.entity.Disease;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.DiseaseRepository;
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
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final MemberRepository memberRepository;
    private final DiseaseRepository diseaseRepository;
    private final ReportSettingService reportSettingService;
    private final ConnectionRepository connectionRepository;

    /**
     * 대상자 본인이 특정 기저질환에 대한 오늘의 수치 입력
     * - mood_check와 달리 하루 여러 번 기록 가능 (단순 INSERT, upsert 아님)
     * - 레포트 마감시간 이후에는 입력 불가
     */
    @Transactional
    public void recordHealth(Long wardId, Long diseaseId, Integer healthValue) {

        // 1. 마감 시간 검증
        LocalTime deadline = reportSettingService.getEffectiveReportTime(wardId);

        if (!LocalTime.now().isBefore(deadline)){
                throw new IllegalStateException("지금은 건강 수치를 입력할 수 없습니다.");
            }

        // 2. member, disease 검증
        Member ward = memberRepository.findById(wardId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 돌봄대상자입니다."));

        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 질병입니다."));

        //3. HealthRecord.builder()로 생성 후 save (upsert 아님, 항상 새로 INSERT)
        HealthRecord healthRecord = HealthRecord.builder()
                .ward(ward)
                .disease(disease)
                .healthValue(healthValue)
                .build();
        healthRecordRepository.save(healthRecord);
    }

    /**
     * 보호자가 대상자의 오늘 건강 수치 기록 목록 조회
     */
    public List<HealthRecordResponseDto> getTodayHealthRecords(Long protectorId, Long wardId) {

        // 1. 권한 검증 (연결 안 돼있으면 예외)
        connectionRepository.findByProtector_MemberIdAndWard_MemberId(protectorId, wardId)
                .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다."));

        // 2. 오늘자 기록 전체 조회
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        List<HealthRecord> records = healthRecordRepository.findAllByWardMemberIdAndRecordedAtBetween(wardId, startOfDay, endOfDay);

        // 3. DTO 리스트로 변환
        return records.stream()
                .map(r -> HealthRecordResponseDto.builder()
                        .diseaseName(r.getDisease().getDiseaseName())
                        .healthValue(r.getHealthValue())
                        .recordedAt(r.getRecordedAt())
                        .build())
                .toList();
    }

}
