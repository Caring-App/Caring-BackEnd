package com.caring.domain.report.entity;

import com.caring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mood_check")
@Getter
@NoArgsConstructor
public class MoodCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mood_check_id")
    private Long id; // 기본키 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Member ward; // 기록한 돌봄대상자

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_status", nullable = false)
    private MoodStatus moodStatus; // 기분 상태 값

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate; // 기록 날짜 -> 하루에 1건만 기록 ( 수정은 여러번 가능 )

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt; // 최종 기록/수정 시간

    @Builder
    // 신규 기분 기록 생성자
    public MoodCheck(Member ward, MoodStatus moodStatus, LocalDate recordDate) {
        this.ward = ward;
        this.moodStatus = moodStatus;
        this.recordDate = recordDate;
        this.checkedAt = LocalDateTime.now();
    }

    // 기분 종류 3가지 - 좋음, 보통, 나쁨
    public enum MoodStatus {
        GOOD, NORMAL, BAD
    }

    // 기분 수정 반영 메소드
    public void updateMood(MoodStatus moodStatus) {
        this.moodStatus = moodStatus;
        this.checkedAt = LocalDateTime.now();
    }
}
