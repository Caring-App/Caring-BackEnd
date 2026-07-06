package com.caring.domain.member.entity;

import lombok.Getter;

@Getter
public enum Provider {
    LOCAL("일반 자체 회원가입"),
    KAKAO("카카오 소셜 로그인"),
    NAVER("네이버 소셜 로그인"),
    GOOGLE("구글 소셜 로그인");

    private final String description;

    Provider(String description) {
        this.description = description;
    }
}
