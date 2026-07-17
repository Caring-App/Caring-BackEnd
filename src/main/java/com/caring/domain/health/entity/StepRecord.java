package com.caring.domain.health.entity;

import com.caring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 걸음 수 기록 엔티티
 * - 기저질환과 무관한 활동량 데이터라 HealthRecord와 분리
 * - 하루에 여러 건 쌓일 수 있는 로그성 테이블
 * - report 도메인의 daily_report 배치에서 오늘자 최신값을 스냅샷으로 가져다 씀
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
    private Integer steps; // 걸음 수

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt; // 기록 시각

    @Builder
    public StepRecord(Member ward, Integer steps) {
        this.ward = ward;
        this.steps = steps;
        this.recordedAt = LocalDateTime.now();
    }
}