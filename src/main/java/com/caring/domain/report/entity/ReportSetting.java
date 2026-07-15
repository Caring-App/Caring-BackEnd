package com.caring.domain.report.entity;

import com.caring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "report_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportSetting {

    /**
     * 보호자별 레포트 수신 시간 설정 엔티티
     * - 돌봄대상자(ward) 1명당 설정 1건 (ward_id 유니크 제약)
     * - report_time이 null이면 기본값 21:00 적용 (애플리케이션 레벨에서 해석)
     * - 이 시간은 동시에 돌봄대상자의 기분/건강 기록 "수정 마감 시간"으로도 사용됨
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_setting_id")
    private Long id; // 기본 키

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false, unique = true)
    private Member ward; // 설정 대상 돌봄대상자

    @Column(name = "report_time")
    private LocalTime reportTime;   // null = 기본값 21:00, 따로 설정 가능

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 마지막 설정 변경 시간

    @Builder
    // 신규 설정 생성자
    public ReportSetting(Member ward, LocalTime reportTime) {
        this.ward = ward;
        this.reportTime = reportTime;
        this.updatedAt = LocalDateTime.now();
    }

    // 레포트 시간 변경 메소드
    public void updateReportTime(LocalTime reportTime) {
        this.reportTime = reportTime;
        this.updatedAt = LocalDateTime.now();
    }

    // 실제 적용 할 레포츠 시간 조회 메소드 ( 설정 값 없으면 기본 값 반환 )
    public LocalTime getEffectiveReportTime() {
        return reportTime != null ? reportTime : LocalTime.of(21, 0);
    }
}
