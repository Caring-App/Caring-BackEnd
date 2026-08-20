package com.caring.domain.policy.repository;

import com.caring.domain.policy.entity.Policy;
import com.caring.domain.policy.entity.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findByType(PolicyType type);
}
