package com.caring.domain.member.repository;

import com.caring.domain.member.entity.MemberDisease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDiseaseRepository extends JpaRepository<MemberDisease, Long> {

}
