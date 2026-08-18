package com.caring.domain.setting.repository;

import com.caring.domain.member.entity.Member;
import com.caring.domain.setting.entity.WardSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WardSettingRepository extends JpaRepository<WardSetting, Long> {
    Optional<WardSetting> findByMember(Member member);
    Optional<WardSetting> findByMember_MemberId(Long memberId);
}
