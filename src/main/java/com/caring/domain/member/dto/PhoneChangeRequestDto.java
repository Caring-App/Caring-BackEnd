package com.caring.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class PhoneChangeRequestDto {

    private String newPhone; // 변경할 새 전화번호
    private String authNumber; // SMS 인증 번호
}
