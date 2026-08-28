package com.caring.domain.pill.repository;

import com.caring.domain.pill.entity.PillSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface PillScheduleRepository extends JpaRepository<PillSchedule, Long> {
    List<PillSchedule> findByWardMemberId(Long wardId);

    @Query("SELECT p FROM PillSchedule p JOIN FETCH p.ward WHERE p.isActive = true AND p.takeTime = :takeTime")
    List<PillSchedule> findActiveSchedulesByTime(@Param("takeTime") LocalTime takeTime);

    List<PillSchedule> findByWard_MemberIdAndIsActiveTrue(Long wardId);
}
