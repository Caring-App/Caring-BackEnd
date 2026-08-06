package com.caring.domain.health.entity;

import com.caring.domain.member.entity.Disease;
import com.caring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 건강 수치 기록 엔티티 (걸음수/혈당/혈압 등)
 * - 대상자가 직접 입력하는 건강 수치 로그
 * - 하루에 여러 건 쌓일 수 있는 로그성 테이블 (mood_check와 달리 유니크 제약 없음)
 * - report 도메인의 daily_report 배치에서 오늘자 최신값을 스냅샷으로 가져다 씀
 */
@Entity
@Table(name = "health_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Member ward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @Column(name = "health_value", nullable = false)
    private Integer healthValue;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Builder
    public HealthRecord(Member ward, Disease disease, Integer healthValue) {
        this.ward = ward;
        this.disease = disease;
        this.healthValue = healthValue;
        this.recordedAt = LocalDateTime.now();

    }
}