package com.caring.domain.connection.repository;

import com.caring.domain.connection.entity.Connection;
import com.caring.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // 이미 연결된 대상자인지 확인
    boolean existsByWard(Member ward);

    // 보호자와의 연결 목록 조회
    List<Connection> findByProtector(Member protector);

    // 이 protector가 이 ward와 실제로 연결되어 있는지 확인
    boolean existsByProtector_MemberIdAndWard_MemberId(Long protectorId, Long wardId);

    // 대상자로 연결된 보호자 찾기 ( 미응답 알림 발송용 )
    Optional<Connection> findByWard(Member ward);

    // 이 protector와 이 ward의 연결 정보 자체를 가져오기 (상세 조회용 - 연결ID, 연결일시 포함)
    Optional<Connection> findByProtector_MemberIdAndWard_MemberId(Long protectorId, Long wardId);
}
