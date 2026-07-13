package com.caring.domain.member.dto;

import com.caring.domain.member.entity.AuthLevel;
import com.caring.domain.member.entity.Provider;
import com.caring.domain.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SocialLoginResponseDto {

    // 기존 회원과 신규 회원의 소셜 로그인 응답을 두가지 경우로 나눠야함

    // 신규 회원이면 true, 기존 회원 로그인 성공이면 false
    private boolean isNewMember;

    // 신규 회원일 때만 채워짐 (프론트가 회원가입 API 호출할 때 다시 보내야 함)
    private Provider provider;
    private String providerId;
    private Role role;

    // 기존 회원 로그인 성공일 때만 채워짐 (LoginResponseDto와 동일한 필드들)
    private Long memberId;
    private String name;
    private String nickname;
    private AuthLevel authLevel;
    private String accessToken;
    private String refreshToken;
}