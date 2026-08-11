package com.caring.domain.health.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.health.dto.HealthGraphResponseDto;
import com.caring.domain.health.dto.HealthRecordResponseDto;
import com.caring.domain.health.entity.HealthRecord;
import com.caring.domain.health.repository.HealthRecordRepository;
import com.caring.domain.health.repository.StepRecordRepository;
import com.caring.domain.member.entity.Disease;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.entity.MemberDisease;
import com.caring.domain.member.repository.DiseaseRepository;
import com.caring.domain.member.repository.MemberDiseaseRepository;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.report.service.ReportSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.caring.domain.health.dto.HealthGraphResponseDto.DailyValueDto;
import com.caring.domain.health.entity.StepRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final MemberRepository memberRepository;
    private final DiseaseRepository diseaseRepository;
    private final ReportSettingService reportSettingService;
    private final ConnectionRepository connectionRepository;
    private final MemberDiseaseRepository memberDiseaseRepository;
    private final StepRecordRepository stepRecordRepository;

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

    /**
     * 보호자가 대상자의 기간별 건강 수치 그래프 조회 (혈당/혈압/걸음수)
     * - 혈당/혈압은 대상자가 해당 질병을 등록한 경우에만 포함, 안 하면 null
     * - 걸음수는 무조건 포함
     * - 하루 여러 건 기록 시 최신값만 사용
     */
    @Transactional(readOnly = true)
    public HealthGraphResponseDto getHealthGraph(Long protectorId, Long wardId, LocalDate startDate, LocalDate endDate) {

        // 1. 권한 검증
        connectionRepository.findByProtector_MemberIdAndWard_MemberId(protectorId, wardId)
                .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다."));

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        // 2. 대상자가 등록한 질병명 목록
        List<MemberDisease> memberDiseases = memberDiseaseRepository.findByWard_MemberId(wardId);
        Set<String> diseaseNames = memberDiseases.stream()
                .map(md -> md.getDisease().getDiseaseName())
                .collect(Collectors.toSet());

        // 3. 혈당 그래프 (당뇨병 등록 시만)
        List<DailyValueDto> bloodSugar = null;
        if (diseaseNames.contains("당뇨병")) {
            Disease diabetes = diseaseRepository.findByDiseaseName("당뇨병")
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질병입니다."));
            List<HealthRecord> records = healthRecordRepository
                    .findAllByWardMemberIdAndDiseaseAndRecordedAtBetweenOrderByRecordedAtAsc(wardId, diabetes, start, end);
            bloodSugar = toDailyLatestList(records);
        }

        // 4. 혈압 그래프 (고혈압 등록 시만)
        List<DailyValueDto> hypertension = null;
        if (diseaseNames.contains("고혈압")) {
            Disease diabetes = diseaseRepository.findByDiseaseName("고혈압")
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질병입니다."));
            List<HealthRecord> records = healthRecordRepository
                    .findAllByWardMemberIdAndDiseaseAndRecordedAtBetweenOrderByRecordedAtAsc(wardId, diabetes, start, end);
            hypertension = toDailyLatestList(records);
        }

        // 5. 걸음수 그래프 (무조건 포함)
        List<StepRecord> stepRecords = stepRecordRepository
                .findAllByWardMemberIdAndRecordedDateBetweenOrderByRecordedDateAsc(wardId, startDate, endDate);
        List<DailyValueDto> steps = stepRecords.stream()
                .map(sr -> DailyValueDto.builder()
                        .date(sr.getRecordedDate())
                        .value(sr.getSteps())
                        .build())
                .collect(Collectors.toList());

        return HealthGraphResponseDto.builder()
                .bloodSugar(bloodSugar)
                .bloodPressure(hypertension)
                .steps(steps)
                .build();
    }

    /**
     * 같은 날짜에 여러 건 있으면 최신값만 남기기
     */
    private List<DailyValueDto> toDailyLatestList(List<HealthRecord> records) {
        Map<LocalDate, HealthRecord> latestByDate = new LinkedHashMap<>();

        for (HealthRecord record : records) {
            LocalDate date = record.getRecordedAt().toLocalDate();
            latestByDate.put(date, record);
        }

        return latestByDate.entrySet().stream()
                .map(entry -> DailyValueDto.builder()
                        .date(entry.getKey())
                        .value(entry.getValue().getHealthValue())
                        .build())
                .collect(Collectors.toList());
    }

}
