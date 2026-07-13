package com.caring.domain.location.entity;

import com.caring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "location_log")
public class LocationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Member ward;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "stay_duration", nullable = false)
    private Integer stayDuration;

    @Column(name = "is_visit_verified", nullable = false)
    private boolean isVisitVerified;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
}
