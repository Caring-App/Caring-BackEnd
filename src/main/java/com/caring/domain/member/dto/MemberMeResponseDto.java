package com.caring.domain.member.dto;

import com.caring.domain.member.entity.Member;
import com.caring.domain.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberMeResponseDto {
    private String name;
    private String phone;
    private String  baseAddress;
    private String  detailAddress;
    private Boolean pushEnabled;
    private Role role;

    public static MemberMeResponseDto of(Member member) {
        return MemberMeResponseDto.builder()
                .name(member.getName())
                .phone(member.getPhone())
                .baseAddress(member.getBaseAddress())
                .detailAddress(member.getDetailAddress())
                .pushEnabled(member.getPushEnabled())
                .role(member.getRole())
                .build();
    }
}
