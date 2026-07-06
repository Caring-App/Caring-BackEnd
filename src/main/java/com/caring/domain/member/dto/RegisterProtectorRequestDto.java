package com.caring.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RegisterProtectorRequestDto {
    private String name;
    private String phone;
    private String authNumber;
    private String password;
    private String passwordCheck;
    private LocalDate birthDate;
    private String address;
}
