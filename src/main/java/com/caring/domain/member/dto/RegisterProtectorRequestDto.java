package com.caring.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class RegisterProtectorRequestDto {
    private String name;
    private String phone;
    private String authNumber;
    private String password;
    private String passwordCheck;
    private String baseAddress;
    private String detailAddress;
}
