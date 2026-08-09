package com.caring.domain.inquiry.dto;

import com.caring.domain.inquiry.entity.Inquiry;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryResponseDto {

    private Long inquiryId;
    private String title;
    private String content;
    private String answer;
    private boolean isAnswered;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;

    // Entity -> Dto 변환
    public static InquiryResponseDto from(Inquiry inquiry) {
        return InquiryResponseDto.builder()
                .inquiryId(inquiry.getInquiryId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .answer(inquiry.getAnswer())
                .isAnswered(inquiry.getIsAnswered())
                .createdAt(inquiry.getCreatedAt())
                .answeredAt(inquiry.getAnsweredAt())
                .build();
    }
}