package com.caring.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenRequestDto {
    // 클라이언트가 accessToken 재발급을 요청할 때 보내는 body를 담는 DTO
    // -> 클라이언트는 로그인 때 받아둔 refreshToken을 여기 담아서 보내면 됨

    private String refreshToken; // 클라이언트가 갖고 있던 refreshToken 그대로 전달받음
}
