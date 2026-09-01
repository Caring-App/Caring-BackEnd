package com.caring.domain.welfarefacility.repository;

import com.caring.domain.welfarefacility.entity.WelfareFacility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WelfareFacilityRepository extends JpaRepository<WelfareFacility, Long> {
    Optional<WelfareFacility> findByFcltCd(String fcltCd);
}
