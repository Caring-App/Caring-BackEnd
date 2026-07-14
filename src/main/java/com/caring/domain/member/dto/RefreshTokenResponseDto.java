package com.caring.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenResponseDto {
    // accessToken 재발급 성공 시 돌려주는 응답 DTO
    // -> refreshToken은 재사용(재발급 안 함)
    // 새로 만든 accessToken만 담으면 됨
    private String accessToken; // 새로 발급된 accessToken
}
