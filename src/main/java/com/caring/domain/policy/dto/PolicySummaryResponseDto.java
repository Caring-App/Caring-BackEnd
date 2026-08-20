package com.caring.domain.policy.dto;

import com.caring.domain.policy.entity.Policy;
import com.caring.domain.policy.entity.PolicyType;
import lombok.Getter;

@Getter
public class PolicySummaryResponseDto {
    private final PolicyType type;
    private final String title;

    public PolicySummaryResponseDto(Policy policy) {
        this.type = policy.getType();
        this.title = policy.getTitle();
    }
}
