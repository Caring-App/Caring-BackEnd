package com.caring.domain.health.entity;

import com.caring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 걸음 수 기록 엔티티
 * - 기저질환과 무관한 활동량 데이터라 HealthRecord와 분리
 * - 하루 1건 upsert 방식 (MoodCheck과 동일 패턴)
 * - steps는 안드로이드에서 계산해 보내주는 "오늘 누적 총 걸음수"를 그대로 저장
 * - report 도메인의 daily_report 배치에서 오늘자 값을 스냅샷으로 가져다 씀
 */
@Entity
@Table(name = "step_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StepRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_record_id")
    private Long id; // 기본키 (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Member ward; // 기록한 돌봄대상자

    @Column(name = "steps", nullable = false)
    private Integer steps; // 오늘 누적 걸음 수

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate; // 기록 날짜 (하루 1건 기준)

    @Builder
    public StepRecord(Member ward, Integer steps) {
        this.ward = ward;
        this.steps = steps;
        this.recordedDate = LocalDate.now();
    }

    public void updateSteps(Integer steps) {
        this.steps = steps;
    }
}