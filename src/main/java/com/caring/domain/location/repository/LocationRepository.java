package com.caring.domain.location.repository;

import com.caring.domain.location.entity.LocationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationRepository extends JpaRepository<LocationLog, Integer> {
    List<LocationLog> findByWard_MemberIdAndRecordedAtAfterOrderByRecordedAtDesc(Long wardId, LocalDateTime after);
    LocationLog findFirstByWard_MemberIdOrderByRecordedAtDesc(Long wardId);
}
