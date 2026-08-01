package com.caring.domain.pill.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="pill_log")
public class PillLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pill_log_id")
    private Long pillLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pill_schedule_id")
    private PillSchedule pillSchedule;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Builder.Default
    @Column(name = "is_taken")
    private boolean isTaken = false;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Builder.Default
    @Column(name = "current_retry_count")
    private Integer currentRetryCount = 0;

    public void confirm(){
        this.isTaken=true;
        this.confirmedAt=LocalDateTime.now();
    }

    public void increaseRetryCount() {
        this.currentRetryCount+=1;
    }

    public void escalate() {
        this.currentRetryCount = ESCALATED;
    }

    public void resetForRetry() {
        this.isTaken = false;
        this.confirmedAt = null;
        this.currentRetryCount = 0;
    }

    public static final int ESCALATED = 4;

}
