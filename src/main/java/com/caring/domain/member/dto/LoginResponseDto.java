package com.caring.domain.member.dto;

import com.caring.domain.member.entity.AuthLevel;
import com.caring.domain.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    private Long memberId;
    private String name;
    private String nickname;
    private Role role;
    private AuthLevel authLevel;
}
