package com.caring.domain.policy.dto;

import com.caring.domain.policy.entity.Policy;
import com.caring.domain.policy.entity.PolicyType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PolicyDetailResponseDto {
    private final PolicyType type;
    private final String title;
    private final String content;
    private final LocalDateTime updatedAt;

    public PolicyDetailResponseDto(Policy policy) {
        this.type = policy.getType();
        this.title = policy.getTitle();
        this.content = policy.getContent();
        this.updatedAt = policy.getUpdatedAt();
    }
}
