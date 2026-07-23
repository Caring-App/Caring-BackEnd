package com.caring.domain.report.service;

import com.caring.domain.health.entity.HealthRecord;
import com.caring.domain.health.repository.HealthRecordRepository;
import com.caring.domain.health.repository.StepRecordRepository;
import com.caring.domain.member.entity.Disease;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.pill.entity.PillLog;
import com.caring.domain.pill.repository.PillLogRepository;
import com.caring.domain.report.entity.DailyReport;
import com.caring.domain.report.entity.DailyReportHealthDetail;
import com.caring.domain.report.repository.DailyReportHealthDetailRepository;
import com.caring.domain.report.repository.DailyReportRepository;
import com.caring.domain.report.repository.MoodCheckRepository;
import com.caring.global.ai.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyReportService {

    private final MoodCheckRepository moodCheckRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final StepRecordRepository stepRecordRepository;
    private final DailyReportRepository dailyReportRepository;
    private final GeminiClient geminiClient;
    private final MemberRepository memberRepository;
    private final PillLogRepository pillLogRepository;
    private final DailyReportHealthDetailRepository dailyReportHealthDetailRepository;

    /**
     * 오늘자 mood/health/step 데이터를 모아 daily_report 한 건 생성
     */
    @Transactional
    public void generateReport(Long wardId) {

        // ward 조회
        Member ward = memberRepository.findById(wardId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 돌봄대상자입니다."));

        LocalDate today = LocalDate.now();

        // 오늘 건강 상태 조회 (없으면 null)
        String moodStatus = moodCheckRepository.findByWardMemberIdAndRecordDate(wardId, today)
                .map(m -> m.getMoodStatus().name())
                .orElse(null);

        // 오늘 걸음 수 조회 (없으면 null)
        Integer step = stepRecordRepository.findByWardMemberIdAndRecordedDate(wardId, today)
                .map(m->m.getSteps())
                .orElse(null);

        // 오늘 health_record 전체 조회
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<HealthRecord> healthRecords = healthRecordRepository.findAllByWardMemberIdAndRecordedAtBetween(wardId,startOfDay,endOfDay);

        // 오늘 기록된 질병별로 그룹핑 → 평균 계산
        Map<Disease, Double> avgByDisease = healthRecords.stream()
                .collect(Collectors.groupingBy(
                        HealthRecord::getDisease,
                        Collectors.averagingInt(HealthRecord::getHealthValue)
                ));

        // ward의 전체 복약 로그 조회
        List<PillLog> pillLog = pillLogRepository.findAllByWardIdAndRecordDate(wardId, today);

        // 복약률 계산
        long takenCount = pillLog.stream().filter(PillLog::isTaken).count();
        int totalCount = pillLog.size();

        Double medicationRate = totalCount == 0
                ? null
                : (double) takenCount / totalCount * 100.0;


        // Gemini 호출
        String prompt = String.format("""
        다음은 %s님의 오늘 하루 데이터입니다. 보호자가 읽을 한 문단짜리 요약을 작성해주세요.
        - 오늘 기분 상태: %s
        - 오늘 복약 이행률: %s
        - 오늘 걸음 수: %s보
        """,
                moodStatus == null ? "체크 안 함" : moodStatus,
                medicationRate == null ? "오늘 복용할 약 없음" : String.format("%.0f%%", medicationRate),
                step == null ? "기록 없음" : step);

        String healthSummary = geminiClient.generateText(prompt);

            // 레포트 생성 후 저장
        DailyReport dailyReport = DailyReport.builder()
                    .ward(ward)
                    .moodStatus(moodStatus)
                    .medicationRate(medicationRate)
                    .steps(step)
                    .healthSummary(healthSummary)
                    .reportDate(today)
                    .build();

        dailyReportRepository.save(dailyReport);

        // 질병별 상세 수치는 여러 건 저장 (질병 개수만큼 반복)
        avgByDisease.forEach((disease, avgValue) -> {
            DailyReportHealthDetail detail = DailyReportHealthDetail.builder()
                    .dailyReport(dailyReport)
                    .disease(disease)
                    .avgValue(avgValue)
                    .build();

            dailyReportHealthDetailRepository.save(detail);
        });

    }
}