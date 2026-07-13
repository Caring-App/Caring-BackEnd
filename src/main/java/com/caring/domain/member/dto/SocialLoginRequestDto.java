package com.caring.domain.member.dto;

import com.caring.domain.member.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class SocialLoginRequestDto {
    // 요청만 소셜 로그인으로 따로 받기에 요청 DTO는 생성 필요
    //

    private String accessToken;
    private Role role;
}
