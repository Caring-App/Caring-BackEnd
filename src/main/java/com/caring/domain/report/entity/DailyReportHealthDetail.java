package com.caring.domain.report.entity;

import com.caring.domain.member.entity.Disease;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_report_health_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyReportHealthDetail {

    /**
     * 일일 레포트의 질병별 상세 수치 엔티티
     * - DailyReport 1건당 여러 질병의 오늘 평균 수치를 담을 수 있음 (1:N)
     * - daily_report_id + disease_id 유니크 제약으로 리포트당 질병 하나에 1건만 존재
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long id; // 기본키 (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_report_id", nullable = false)
    private DailyReport dailyReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @Column(name = "avg_value", nullable = false)
    private Double avgValue; // 오늘 해당 질병의 평균 수치

    @Builder
    public DailyReportHealthDetail(DailyReport dailyReport, Disease disease, Double avgValue) {

        this.dailyReport=dailyReport;
        this.disease=disease;
        this.avgValue=avgValue;

    }
}