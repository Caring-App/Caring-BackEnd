package com.caring.domain.connection.repository;

import com.caring.domain.connection.entity.Connection;
import com.caring.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // 이미 연결된 대상자인지 확인
    boolean existsByWard(Member ward);

    // 보호자와의 연결 목록 조회
    List<Connection> findByProtector(Member protector);

    // 이 protector가 이 ward와 실제로 연결되어 있는지 확인
    boolean existsByProtector_MemberIdAndWard_MemberId(Long protectorId, Long wardId);
}
