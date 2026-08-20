package com.caring.domain.policy.service;

import com.caring.domain.policy.dto.PolicyDetailResponseDto;
import com.caring.domain.policy.dto.PolicySummaryResponseDto;
import com.caring.domain.policy.entity.Policy;
import com.caring.domain.policy.entity.PolicyType;
import com.caring.domain.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PolicyService {
    private final PolicyRepository policyRepository;

    public List<PolicySummaryResponseDto> getPolicyList() {
        return policyRepository.findAll()
                .stream()
                .map(PolicySummaryResponseDto::new)
                .toList();
    }


    public PolicyDetailResponseDto getPolicyDetail(PolicyType type) {
        Policy policy = policyRepository.findByType(type)
                .orElseThrow(() -> new IllegalArgumentException("해당 약관을 찾을 수 없습니다."));
        return new PolicyDetailResponseDto(policy);
    }
}
