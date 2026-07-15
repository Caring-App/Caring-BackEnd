package com.caring.domain.report.entity;

import com.caring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyReport {

    /**
     * 일일 요약 레포트 엔티티
     * - 매일 배치(DailyReportScheduler)가 ward별로 생성
     * - ward_id + report_date 유니크 제약으로 하루에 1건만 존재
     * - 생성 시점엔 is_delivered = false, 실제 FCM 발송 성공 후 markAsDelivered() 호출
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_report_id")
    private Long id; // 기본키 (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Member ward; // 레포트 대상 돌봄대상자

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate; // 레포트 기준 날짜

    @Column(name = "mood_status")
    private String moodStatus;   // MoodCheck.MoodStatus를 문자열로 스냅샷 (좋음/보통/나쁨), 미체크 시 null

    @Column(name = "steps")
    private Integer steps;       // 오늘의 걸음 수

    @Column(name = "blood_sugar_value")
    private Integer bloodSugarValue;   // 오늘의 혈당 수치, 미입력 시 null

    @Column(name = "blood_pressure_value")
    private Integer bloodPressureValue;   // 오늘의 혈압 수치, 미입력 시 null

    @Column(name = "health_summary")
    private String healthSummary; // 당일 기분/건강 상태 요약 텍스트

    @Column(name = "medication_rate")
    private Double medicationRate; // 당일 복약 이행률 (%)

    @Column(name = "is_delivered", nullable = false)
    private Boolean isDelivered; // 보호자에게 실제 전달(FCM 발송)됐는지 여부

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt; // 레포트 생성 시각

    @Builder
    public DailyReport(Member ward, LocalDate reportDate, String moodStatus, Integer steps,
                       Integer bloodSugarValue, Integer bloodPressureValue,
                       String healthSummary, Double medicationRate) {
        this.ward = ward;
        this.reportDate = reportDate;
        this.moodStatus = moodStatus;
        this.steps = steps;
        this.bloodSugarValue = bloodSugarValue;
        this.bloodPressureValue = bloodPressureValue;
        this.healthSummary = healthSummary;
        this.medicationRate = medicationRate;
        this.isDelivered = false;
        this.generatedAt = LocalDateTime.now();
    }

    public void markAsDelivered() {
        this.isDelivered = true;
    }
}


