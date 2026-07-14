package com.caring.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetRequestDto {

    private String phone; // 인증용 전화번호
    private String authNumber; // 인증 번호
    private String newPassword; // 새 비밀번호
    private String newPasswordCheck; // 새 비밀번호 확인
}
