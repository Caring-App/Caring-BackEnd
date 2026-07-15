package com.caring.domain.member.repository;

import com.caring.domain.member.entity.MemberDisease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberDiseaseRepository extends JpaRepository<MemberDisease, Long> {
    List<MemberDisease> findByWard_MemberId(Long wardId);
}
