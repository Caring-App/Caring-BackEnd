package com.caring.domain.policy.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PolicyType {
    TERMS_OF_SERVICE("이용약관"),
    PRIVACY_POLICY("개인정보처리방침"),
    LOCATION_SERVICE("위치기반서비스 이용약관"),
    HEALTH_INFO_CONSENT("건강정보 처리 동의");

    private final String description;
}