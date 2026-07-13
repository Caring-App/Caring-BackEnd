package com.caring.domain.member.repository;

import com.caring.domain.member.entity.Member;
import com.caring.domain.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByProtectorCode(String protectorCode);
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);}