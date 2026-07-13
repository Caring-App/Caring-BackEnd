package com.caring.domain.member.dto;

import com.caring.domain.member.entity.Provider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SocialUserInfo {

    // 소셜 로그인 응답을 담기는 하는 용도라 setter 필요 X
    private final Provider provider; // 어느 소셜 로그인인지
    private final String providerId; // 소셜에서의 고유 ID
}
