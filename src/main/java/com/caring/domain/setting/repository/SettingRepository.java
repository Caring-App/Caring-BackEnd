package com.caring.domain.setting.repository;

import com.caring.domain.member.entity.Member;
import com.caring.domain.setting.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByMember(Member member);
    Optional<Setting> findByMember_MemberId(Long memberId);
}
