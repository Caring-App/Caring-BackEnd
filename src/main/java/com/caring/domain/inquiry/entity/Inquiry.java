package com.caring.domain.inquiry.entity;

import com.caring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer; // 답변 전엔 null

    @Column(name = "is_answered", nullable = false)
    private Boolean isAnswered;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Builder
    public Inquiry(Member member,String title,String content) {
        this.member=member;
        this.title=title;
        this.content=content;
        this.answer = null;              // 답변 전이니까 null
        this.isAnswered = false;         // 아직 답변 안 됨
        this.answeredAt = null;          // 답변 전이니까 null
        this.createdAt = LocalDateTime.now();
    }

    // 답변 등록 메소드
    public void registerAnswer(String answer) {
        this.answer = answer;
        this.isAnswered = true;
        this.answeredAt = LocalDateTime.now();
    }
}
