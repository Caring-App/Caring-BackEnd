package com.caring.domain.policy.entity;

import com.google.cloud.audit.ViolationInfo;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "policy")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, unique = true, length = 50)
    private PolicyType type;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Policy(PolicyType type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}

