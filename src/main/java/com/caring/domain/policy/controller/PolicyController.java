package com.caring.domain.policy.controller;

import com.caring.domain.policy.dto.PolicyDetailResponseDto;
import com.caring.domain.policy.dto.PolicySummaryResponseDto;
import com.caring.domain.policy.entity.PolicyType;
import com.caring.domain.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policy")
public class PolicyController {
    private final PolicyService policyService;

    @GetMapping
    public ResponseEntity<List<PolicySummaryResponseDto>> getPolicyList() {
        return ResponseEntity.ok(policyService.getPolicyList());
    }

    @GetMapping("/{type}")
    public ResponseEntity<PolicyDetailResponseDto> getPolicyDetail(@PathVariable PolicyType type) {
        return ResponseEntity.ok(policyService.getPolicyDetail(type));
    }
}
